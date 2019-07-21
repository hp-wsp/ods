package com.ts.server.ods.kaptcha;

import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.google.code.kaptcha.util.Config;
import com.ts.server.ods.BaseException;
import com.ts.server.ods.kaptcha.code.KaptchaCodeService;
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
    private static final int EXPIRED_MILLS = 5 * 60 * 1000;
    private final Producer kaptcha;
    private final KaptchaCodeService codeService;

    @Autowired
    public KaptchaService(KaptchaCodeService codeService){
        this.kaptcha = kaptcha();
        this.codeService = codeService;
    }

    private DefaultKaptcha kaptcha(){
        DefaultKaptcha kaptcha = new DefaultKaptcha();
        kaptcha.setConfig(config());
        return kaptcha;
    }

    private Config config(){
        Properties properties = new Properties();
        properties.setProperty("kaptcha.border", "yes");
        properties.setProperty("kaptcha.border.color", "105,179,90");
        properties.setProperty("kaptcha.image.width", "125");
        properties.setProperty("kaptcha.image.height", "45");
        properties.setProperty("kaptcha.textproducer.font.color", "blue");
        properties.setProperty("kaptcha.textproducer.font.size", "45");
        properties.setProperty("kaptcha.textproducer.char.length", "4");
        return new Config(properties);
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
        Optional<String>   optional = codeService.get(codeKey);
        return optional.map(e -> StringUtils.equals(value, e)).orElse(false);
    }

    @Scheduled(fixedDelay = 60 * 1000)
    public void clearExpired(){
        codeService.clearExpired(new Date(System.currentTimeMillis() - EXPIRED_MILLS ));
    }
}
