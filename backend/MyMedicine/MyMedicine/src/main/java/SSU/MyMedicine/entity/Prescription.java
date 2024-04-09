package SSU.MyMedicine.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
public class Prescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pid")
    private Integer pid;

    @Column(name = "reg_date")
    private LocalDateTime regDate;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "image_num")
    private Integer imageNum;

    @ManyToOne
    @JoinColumn(name = "uid")
    private User user;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "MedList",
            joinColumns = @JoinColumn(name = "pid"),
            inverseJoinColumns = @JoinColumn(name = "mid"))
    private List<Medicine> medList;
}
