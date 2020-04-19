package com.amsidh.mvc;

import com.amsidh.mvc.document.Employee;
import com.amsidh.mvc.document.EmployeeEvent;
import com.amsidh.mvc.repository.EmployeeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

import java.util.Date;
import java.util.stream.Stream;

import static java.time.Duration.ofSeconds;
import static java.util.UUID.randomUUID;
import static java.util.stream.Stream.generate;
import static reactor.core.publisher.Flux.interval;
import static reactor.core.publisher.Flux.zip;

@SpringBootApplication
public class SpringReactiveMongoWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringReactiveMongoWebApplication.class, args);
    }

    @Bean
    CommandLineRunner getCommandLineRunner(EmployeeRepository employeeRepository) {
        return args -> {
            employeeRepository.deleteAll().subscribe(null, null, () -> {
                Stream.of(new Employee(randomUUID().toString(), "Amsidh", 125L),
                        new Employee(randomUUID().toString(), "Sanjay", 25L),
                        new Employee(randomUUID().toString(), "Raju", 45L),
                        new Employee(randomUUID().toString(), "Dada", 34L),
                        new Employee(randomUUID().toString(), "Adithi", 0L),
                        new Employee(randomUUID().toString(), "Anjali", 0L),
                        new Employee(randomUUID().toString(), "Adity", 0L),
                        new Employee(randomUUID().toString(), "Manakshi", 0L),
                        new Employee(randomUUID().toString(), "Pintu", 0L)
                ).forEach(employee -> employeeRepository.save(employee).subscribe(System.out::println));
            });

        };
    }

    @Bean
    RouterFunction<ServerResponse> getRouteFunction(EmployeeRepository employeeRepository) {
        return RouterFunctions.route(RequestPredicates.GET("/rest/employees"), request -> {
            return ServerResponse.ok().body(employeeRepository.findAll(), Employee.class);
        }).andRoute(RequestPredicates.GET("/rest/employees/{employeeId}"), request -> {
            return ServerResponse.ok().body(employeeRepository.findById(request.pathVariable("employeeId")), Employee.class);
        }).andRoute(RequestPredicates.GET("/rest/employees/{employeeId}/events"), request -> {
            return ServerResponse.ok().contentType(MediaType.TEXT_EVENT_STREAM)
                    .body(employeeRepository.findById(request.pathVariable("employeeId")).flatMapMany(employee -> {
                        Flux<Long> intervalFlux = interval(ofSeconds(2));
                        Flux<EmployeeEvent> employeeEventFlux = Flux
                                .fromStream(generate(() -> new EmployeeEvent(employee, new Date())));
                        return zip(intervalFlux, employeeEventFlux).map(Tuple2::getT2);
                    }), EmployeeEvent.class);
        });

    }
}
