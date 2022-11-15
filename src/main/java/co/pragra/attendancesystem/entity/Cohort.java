package co.pragra.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Cohort {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String name;
    @Column(name = "COURSE_NAME", nullable = false)
    private String courseName;
    @Column(name = "START_DATE", nullable = false)
    private Date startDate;
    @Column(name = "END_DATE", nullable = false)
    private Date endDate;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Student> students;

    @ManyToMany(cascade = CascadeType.ALL)
    private List<Session> sessions;
}
