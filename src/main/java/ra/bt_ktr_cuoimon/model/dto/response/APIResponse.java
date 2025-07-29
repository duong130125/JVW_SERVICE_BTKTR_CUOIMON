package ra.bt_ktr_cuoimon.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class APIResponse <T>{
    private Boolean success;
    private String message;
    private T data;
    private HttpStatus status;
}
