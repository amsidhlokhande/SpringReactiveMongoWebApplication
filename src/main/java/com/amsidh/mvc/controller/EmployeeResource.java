package com.amsidh.mvc.controller;

import com.amsidh.mvc.document.Employee;
import com.amsidh.mvc.document.EmployeeEvent;
import com.amsidh.mvc.repository.EmployeeRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.util.Date;

import static java.time.Duration.ofSeconds;
import static java.util.stream.Stream.generate;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.publisher.Flux.zip;

@RestController
@RequestMapping("/rest/employee")
public class EmployeeResource {

    private EmployeeRepository employeeRepository;

    public EmployeeResource(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping(value = "/all")
    public Flux<Employee> getAllEmployee() {

        return employeeRepository.findAll();
    }

    @GetMapping(value = "/{id}")
    public Mono<Employee> getEmployeeById(@PathVariable("id") final String id) {

        return employeeRepository.findById(id);
    }

    @GetMapping(value = "/{id}/events", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<EmployeeEvent> getEvents(@PathVariable("id") final String empId) {

        return employeeRepository.findById(empId)
                .flatMapMany(employee -> {
                    Flux<Long> intervalFlux = interval(ofSeconds(2));
                    Flux<EmployeeEvent> employeeEventFlux = Flux
                            .fromStream(generate(() -> new EmployeeEvent(employee, new Date())));
                    return zip(intervalFlux, employeeEventFlux).map(Tuple2::getT2);
                });
    }

}
