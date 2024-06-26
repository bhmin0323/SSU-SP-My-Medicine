package SSU.MyMedicine.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
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

    @Column(name = "warning")
    private String warning;
}
