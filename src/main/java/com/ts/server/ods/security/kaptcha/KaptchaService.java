package com.ts.server.ods.security.kaptcha;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.ts.server.ods.BaseException;
import com.ts.server.ods.security.kaptcha.code.KaptchaCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Optional;
import java.util.Properties;

/**
 * 验证码服务
 *
 * @author <a href="mailto:hhywangwei@gmail.com">WangWei</a>
 */
@Service
public class KaptchaService {
    private static final int EXPIRED_MILLS = 15 * 60 * 1000;
    private final Producer kaptcha;
    private final KaptchaCodeService codeService;

    @Autowired
    public KaptchaService(KaptchaProperties properties, KaptchaCodeService codeService){
        this.kaptcha = kaptcha(properties);
        this.codeService = codeService;
    }

    private DefaultKaptcha kaptcha(KaptchaProperties properties){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config(properties));
        return kaptcha;
    }

    private Config config(KaptchaProperties properties){
        Properties pro = new Properties();
        pro.setProperty("kaptcha.border", properties.isBorder()? "yes": "no");
        pro.setProperty("kaptcha.border.color", properties.getBorderColor());
        pro.setProperty("kaptcha.image.width", String.valueOf(properties.getWidth()));
        pro.setProperty("kaptcha.image.height", String.valueOf(properties.getHeight()));
        pro.setProperty("kaptcha.textproducer.font.color", properties.getFontColor());
        pro.setProperty("kaptcha.textproducer.font.size", String.valueOf(properties.getFontSize()));
        pro.setProperty("kaptcha.textproducer.char.length", String.valueOf(properties.getCharLength()));
        return new Config(pro);
    }

    public void writCodeImage(String codeKey, OutputStream outputStream)throws IOException {
        String code = kaptcha.createText();
        try{
            codeService.save(codeKey, code);
        }catch (DataAccessException e){
            throw new BaseException("验证码KEY已经存在");
        }

        BufferedImage bi = kaptcha.createImage(code);
        ImageIO.write(bi, "jpg", outputStream);
    }

    public boolean validate(String codeKey, String value){
        Optional<String> optional = codeService.get(codeKey);
        boolean ok = optional.map(e -> StringUtils.equals(value, e)).orElse(false);
        if(ok){
            codeService.delete(codeKey);
        }
        return ok;
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void clearExpired(){
        codeService.clearExpired(new Date(System.currentTimeMillis() - EXPIRED_MILLS ));
    }
}
