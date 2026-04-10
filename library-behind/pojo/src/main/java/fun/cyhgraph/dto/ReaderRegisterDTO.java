package fun.cyhgraph.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReaderRegisterDTO implements Serializable {

    private String name;
    private String phone;
    private String password;
    private String email;
    private String notes;
    private String wAddress;
    private String hAddress;
}
