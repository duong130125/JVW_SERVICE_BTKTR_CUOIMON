package ra.bt_ktr_cuoimon.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DataErrorResponse<T> {
    private LocalDateTime timestamp;
    private int statusCode;
    private String status;
    private String path;
    private String message;
    private T errors;
}