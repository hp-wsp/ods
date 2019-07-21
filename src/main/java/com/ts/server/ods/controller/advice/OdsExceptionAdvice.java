package com.ts.server.ods.controller.advice;

import com.ts.server.ods.BaseException;
import com.ts.server.ods.controller.vo.ResultVo;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

/**
 * BaseException拦截处理
 *
 * @author <a href="hhywangwei@gmail.com">WangWei</a>
 */
@ControllerAdvice(annotations = RestController.class)
public class OdsExceptionAdvice {
    private static final Logger LOGGER = LoggerFactory.getLogger(OdsExceptionAdvice.class);

    @ExceptionHandler(BaseException.class)
    @ResponseBody
    @SuppressWarnings("unused")
    public ResultVo<String> handleBaseException(BaseException e){
        LOGGER.error("Controller error code {} message {}", e.getCode(), e.getMessage());
        return ResultVo.error(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(ConversionFailedException.class)
    @ResponseBody
    @SuppressWarnings("unused")
    public ResultVo<String> handlerConversionFailedException(ConversionFailedException e){
        LOGGER.error("Parameter conversion fail, error is {}", e.getMessage());
        return ResultVo.error(100, "参数错误");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ResultVo<String> handlerMethodArgumentNotValidException(MethodArgumentNotValidException e){
        LOGGER.error("Parameter valid fail, error is {}", e.getMessage());
        BindingResult result = e.getBindingResult();
        String[] messages = result.getFieldErrors().stream()
                .map(f -> f.getField() + "=" + f.getDefaultMessage()).toArray(String[]::new);
        return ResultVo.error(1001, messages);
    }
}
