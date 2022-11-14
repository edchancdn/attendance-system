package co.pragra.attendancesystem.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Time;
import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "SESSION_DATE", nullable = false)
    private Date sessionDate;
    @Column(name = "START_TIME", nullable = false)
    private Time startTime;
    @Column(name = "END_TIME", nullable = false)
    private Time endTime;
    @ManyToMany
    private List<Student> attendedStudents;
}
