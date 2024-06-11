package org.iot.hotelitybackend.customer.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.iot.hotelitybackend.customer.aggregate.NationEntity;
import org.iot.hotelitybackend.customer.dto.NationDTO;
import org.iot.hotelitybackend.customer.repository.NationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
public class NationServiceImpl implements NationService{

	private final NationRepository nationRepository;
	private final ModelMapper mapper;

	public NationServiceImpl(NationRepository nationRepository, ModelMapper mapper) {
		this.nationRepository = nationRepository;
		this.mapper = mapper;
	}

	@Override
	public List<NationDTO> selectNationList() {
		List<NationEntity> nationEntities = nationRepository.findAll();
		if (nationEntities == null) {
			return Collections.emptyList();
		}
		return nationEntities.stream()
			.map(entity -> mapper.map(entity, NationDTO.class))
			.collect(Collectors.toList());
	}
}
