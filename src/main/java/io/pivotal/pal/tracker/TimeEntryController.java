package io.pivotal.pal.tracker;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/time-entries")
public class TimeEntryController {

    private final CounterService counterService;
    private final GaugeService gaugeService;
    private final TimeEntryRepository timeEntryRepository;

    public TimeEntryController(CounterService counterService,
                               GaugeService gaugeService,
                               TimeEntryRepository timeEntryRepository) {
        this.counterService = counterService;
        this.gaugeService = gaugeService;
        this.timeEntryRepository = timeEntryRepository;
    }

    @PostMapping
    public ResponseEntity create(@RequestBody TimeEntry timeEntryToCreate) {
        TimeEntry timeEntry = timeEntryRepository.create(timeEntryToCreate);

        counterService.increment("TimeEntry.created");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());

        return ResponseEntity.status(HttpStatus.CREATED).body(timeEntry);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TimeEntry> read(@PathVariable long id) {
        TimeEntry timeEntry = timeEntryRepository.find(id);

        if (timeEntry == null) {
            return ResponseEntity.notFound().build();
        }

        counterService.increment("TimeEntry.read");

        return ResponseEntity.ok(timeEntry);
    }

    @GetMapping
    public ResponseEntity<List<TimeEntry>> list() {
        List<TimeEntry> timeEntries = timeEntryRepository.list();

        counterService.increment("TimeEntry.listed");

        return ResponseEntity.ok(timeEntries);
    }

    @PutMapping("/{id}")
    public ResponseEntity<TimeEntry> update(@PathVariable long id, @RequestBody TimeEntry timeEntry) {
        TimeEntry update = timeEntryRepository.update(id, timeEntry);

        if (update == null) {
            return ResponseEntity.notFound().build();
        }

        counterService.increment("TimeEntry.updated");

        return ResponseEntity.ok(update);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TimeEntry> delete(@PathVariable long id) {
        timeEntryRepository.delete(id);

        counterService.increment("TimeEntry.deleted");
        gaugeService.submit("timeEntries.count", timeEntryRepository.list().size());

        return ResponseEntity.noContent().build();
    }
}
