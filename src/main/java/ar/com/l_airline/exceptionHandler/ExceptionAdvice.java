package ar.com.l_airline.exceptionHandler;

import ar.com.l_airline.exceptionHandler.custom_exceptions.ConflictStateException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.MissingDataException;
import ar.com.l_airline.exceptionHandler.custom_exceptions.NotFoundInDatabaseException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionAdvice {
    /**
     *  Return a 400 code if required data was not received.
     * @return 400, with custom message.
     */
    @ExceptionHandler(value = MissingDataException.class)
    public ResponseEntity<ExceptionDTO> missingDataExcHandler(MissingDataException e) {
        ExceptionDTO dto = ExceptionDTO.builder().message(e.getMessage())
                .code(HttpStatusCode.valueOf(400)).build();
        return new ResponseEntity<>(dto,dto.getCode());
    }
    /**
     *  Return a 404 code if the requested data was not found.
     * @return 404, with custom message.
     */
    @ExceptionHandler(value = NotFoundInDatabaseException.class)
    public ResponseEntity<ExceptionDTO> notFoundInDatabaseExcHandler(NotFoundInDatabaseException e) {
        ExceptionDTO dto = ExceptionDTO.builder().message(e.getMessage())
                .code(HttpStatusCode.valueOf(404)).build();
        return new ResponseEntity<>(dto,dto.getCode());
    }
    /**
     *  Return a 400 code the register status does not allow changes.
     * @return 400, with custom message.
     */
    @ExceptionHandler(value = ConflictStateException.class)
    public ResponseEntity<ExceptionDTO> conflictStateExcHandler(ConflictStateException e) {
        ExceptionDTO dto = ExceptionDTO.builder().message(e.getMessage())
                .code(HttpStatusCode.valueOf(400)).build();
        return new ResponseEntity<>(dto,dto.getCode());
    }


}
