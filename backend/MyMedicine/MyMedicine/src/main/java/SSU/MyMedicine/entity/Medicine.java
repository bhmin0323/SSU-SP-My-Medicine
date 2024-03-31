package SSU.MyMedicine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mid")
    private Integer mid;

    @Column(name = "med_name")
    private String medName;

    @Column(name = "med_comp")
    private String medComp;
}
