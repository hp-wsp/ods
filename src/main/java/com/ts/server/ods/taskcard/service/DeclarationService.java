package com.ts.server.ods.taskcard.service;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.OdsProperties;
import com.ts.server.ods.base.domain.Member;
import com.ts.server.ods.base.domain.Resource;
import com.ts.server.ods.base.service.MemberService;
import com.ts.server.ods.base.service.ResourceService;
import com.ts.server.ods.common.id.IdGenerators;
import com.ts.server.ods.common.word.WordExport;
import com.ts.server.ods.common.word.WordExportHtmlFactory;
import com.ts.server.ods.taskcard.dao.DeclarationDao;
import com.ts.server.ods.taskcard.domain.Declaration;
import com.ts.server.ods.taskcard.domain.TaskCard;
import com.ts.server.ods.taskcard.domain.TaskCardItem;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

/**
 * 任务指标申报材料业务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
@Transactional(readOnly = true)
public class DeclarationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(DeclarationService.class);

    private final DeclarationDao dao;
    private final TaskItemService itemService;
    private final TaskCardService cardServer;
    private final ResourceService resourceService;
    private final OdsProperties properties;
    private final MemberService memberService;
    private final ExportWordToHtml exportWordToHtml;

    @Autowired
    public DeclarationService(DeclarationDao dao, TaskItemService itemService,
                              TaskCardService cardServer, ResourceService resourceService,
                              MemberService memberService, OdsProperties properties) {
        this.dao = dao;
        this.itemService = itemService;
        this.cardServer = cardServer;
        this.resourceService = resourceService;
        this.memberService = memberService;
        this.properties = properties;
        this.exportWordToHtml = new ExportWordToHtml(properties);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Declaration save(String taskItemId, String memberId, MultipartFile file){
        TaskCardItem item = itemService.get(taskItemId);
        TaskCard card = cardServer.get(item.getCardId());

        if(!card.isOpen()){
            throw new BaseException("测评未开启");
        }

        if(card.getStatus() == TaskCard.Status.SUBMIT){
            throw new BaseException("测评已经提交，请等待审核");
        }

        if(card.getStatus() == TaskCard.Status.GRADE){
            throw new BaseException("测评已经审核，如需修改请联系管理员");
        }

        if(!StringUtils.equals(card.getDecId(), memberId)){
            throw new BaseException(406, "权限错误不能申报材料");
        }


        String id = IdGenerators.uuid();
        String viewUrl = exportWordToHtml.export(file, id).orElse("");

        String path = saveFile(card, id, file).orElseThrow(() -> new BaseException("上传申报文件失败") );
        Declaration t = new Declaration();
        t.setId(IdGenerators.uuid());
        String filename = StringUtils.remove(file.getOriginalFilename(), " ");
        LOGGER.debug("Declaration filename itemId={}, filename={}", id, filename);
        t.setFileName(filename);
        t.setFileSize((int)file.getSize());
        t.setCardId(item.getCardId());
        t.setCardItemId(item.getId());
        t.setEvaItemId(item.getEvaItemId());
        t.setPath(path);
        LOGGER.debug("Upload file contentType={}", file.getContentType());
        t.setContentType(file.getContentType());
        Member member = memberService.get(memberId);
        t.setDecUsername(member.getUsername());

        dao.insert(t);

        saveResource(t, viewUrl);

        itemService.updateDeclare(item.getId(), true);
        if(StringUtils.isNotBlank(item.getGradeLevel())){
            itemService.clearGrade(item.getId());
        }

        return dao.findOne(t.getId());
    }

    private Optional<String> saveFile(TaskCard card, String id, MultipartFile file) {
        try{
            String dir = properties.getResource()+ "/" + card.getEvaId() + "/" + card.getId();
            LOGGER.debug("Save file cardId={},dir={}", card.getId(), dir);
            File f = new File(dir);
            if(!f.exists()){
                boolean ok = f.mkdirs();
                LOGGER.debug("Create dir cardId={}, path={}, success={}", card.getId(), dir, ok);
            }

            String path = dir + "/" + id;
            file.transferTo(new File(path));
            LOGGER.debug("Copy file taskId={}, path={}", card.getId(), path);

            return Optional.of(path);
        }catch (IOException e){
            LOGGER.error("Upload file cardId={},cardItemId={},throw={}", card.getId(), id, e.getMessage());
            return Optional.empty();
        }

    }

    private void saveResource(Declaration declaration, String viewUrl){
        Resource t = new Resource();

        t.setId(declaration.getId());
        t.setPath(declaration.getPath());
        t.setFileName(declaration.getFileName());
        t.setFileSize(declaration.getFileSize());
        t.setContentType(declaration.getContentType());
        t.setType("declaration");
        t.setViewUrl(viewUrl);

        resourceService.save(t);
    }

    public Declaration get(String id){
        try{
            return dao.findOne(id);
        }catch (DataAccessException e){
            throw new BaseException("申报资料不存在");
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean delete(String id, String memberId){
        Declaration t = get(id);
        TaskCardItem item = itemService.get(t.getCardItemId());
        TaskCard card = cardServer.get(item.getCardId());

        if(!card.isOpen()){
            throw new BaseException("测评未开启");
        }

        if(card.getStatus() == TaskCard.Status.SUBMIT){
            throw new BaseException("测评已经提交，请等待审核");
        }

        if(card.getStatus() == TaskCard.Status.GRADE){
            throw new BaseException("测评已经审核，如需修改请联系管理员");
        }

        if(!StringUtils.equals(card.getDecId(), memberId)){
            throw new BaseException(406, "权限错误不能该删除申报材料");
        }

        boolean ok = dao.delete(id);
        if(ok){
            itemService.updateDeclare(t.getCardItemId(), dao.hasByItemId(t.getCardItemId()));
        }

        return ok;
    }

    public List<Declaration> queryByCardId(String cardId){
        return dao.findByCardId(cardId);
    }

    public List<Declaration> queryByEvaItemId(String evaItemId){
        return dao.findByEvaItemId(evaItemId);
    }

    public List<Declaration> queryByItemId(String itemId){
        return dao.findByItemId(itemId);
    }

    /**
     * 导出Word到html网页
     */
    static class ExportWordToHtml {
        private final OdsProperties properties;
        private final WordExportHtmlFactory factory;

        ExportWordToHtml(OdsProperties properties){
            this.properties = properties;
            this.factory = new WordExportHtmlFactory(properties.getViewDir(), properties.getViewUrl());
        }

        Optional<String> export(MultipartFile file, String id){
            Optional<WordExport> optional = factory.getContextType(file.getContentType());
            return optional.flatMap(e -> {
                try(InputStream in = file.getInputStream()){
                    String n = optional.get().export(in, id);
                    return Optional.of(String.format("%s/%s/%s", properties.getViewUrl(), id, n));
                }catch(Exception ex){
                    LOGGER.error("Export word html fail id={},throws={}", id, ex.getMessage());
                    return Optional.empty();
                }
            });
        }
    }
}
