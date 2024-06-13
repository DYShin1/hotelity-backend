package org.iot.hotelitybackend.customer.service;

import org.iot.hotelitybackend.customer.aggregate.NationEntity;
import org.iot.hotelitybackend.customer.dto.NationDTO;
import org.iot.hotelitybackend.customer.repository.NationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

class NationServiceImplTest {

	@Mock
	private NationRepository nationRepository;

	@Mock
	private ModelMapper mapper;

	@InjectMocks
	private NationServiceImpl nationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSelectNationList() {
		// Given
		List<NationEntity> nationEntities = Arrays.asList(NationEntity.builder().build());
		when(nationRepository.findAll()).thenReturn(nationEntities);
		when(mapper.map(nationEntities, List.class)).thenReturn(Arrays.asList(new NationDTO()));

		// When
		List<NationDTO> result = nationService.selectNationList();

		// Then
		assertNotNull(result);
		verify(nationRepository, times(1)).findAll();
	}
}