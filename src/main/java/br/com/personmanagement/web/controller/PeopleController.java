package br.com.personmanagement.web.controller;

import br.com.personmanagement.application.dto.request.PeopleRequestDTO;
import br.com.personmanagement.application.dto.response.PeopleResponseDTO;
import br.com.personmanagement.domain.entity.People;
import br.com.personmanagement.domain.service.PeopleService;
import br.com.personmanagement.infrastructure.assembler.PeopleConverter;
import br.com.personmanagement.infrastructure.repository.PeopleRepository;
import javax.validation.Valid;

import br.com.personmanagement.infrastructure.specifications.PeopleSpecifications;
import br.com.personmanagement.web.exception.BusinessException;
import br.com.personmanagement.web.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/people")
public class PeopleController {

    private final PeopleConverter peopleConverter;
    private final PeopleService peopleService;

    @Autowired
    PeopleRepository peopleRepository;

    public PeopleController(PeopleConverter peopleConverter, PeopleService peopleService) {
        this.peopleConverter = peopleConverter;
        this.peopleService = peopleService;
    }

    @GetMapping
    public List<PeopleResponseDTO> listPeople(PeopleSpecifications peopleSpecifications) {
        return peopleConverter.toCollectionDTO(peopleService.getAllPeople(peopleSpecifications));
    }

    @GetMapping("/{peopleId}")
    public PeopleResponseDTO getPeopleId(@PathVariable Long peopleId) {
        People people = peopleService.searchOrFail(peopleId);
        return peopleConverter.toDto(people);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PeopleResponseDTO createPeople(@RequestBody @Valid PeopleRequestDTO peopleRequestDTO) {
        try {
            return peopleConverter.toDto(peopleService.create(peopleRequestDTO));
        }
        catch (ResourceNotFoundException e){
                throw new BusinessException(e.getMessage());
        }
    }

    @PutMapping("/{peopleId}")
    public PeopleResponseDTO update(@PathVariable Long peopleId, @RequestBody @Valid PeopleRequestDTO peopleRequestDTO) {
        try {
            People updatedPeople = peopleService.searchOrFail(peopleId);

            peopleConverter.copyToDomainObject(peopleRequestDTO, updatedPeople);

            return peopleConverter.toDto(peopleService.create(peopleRequestDTO));
        }
        catch (ResourceNotFoundException e){
            throw new BusinessException(e.getMessage());
        }
    }
}
