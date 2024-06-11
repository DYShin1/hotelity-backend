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

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class NationServiceImplTests {

	@Mock
	private ModelMapper mapper;

	@Mock
	private NationRepository nationRepository;

	@InjectMocks
	private NationServiceImpl nationService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testSelectNationList() {
		NationEntity nationEntity = NationEntity.builder().build();
		when(nationRepository.findAll()).thenReturn(Arrays.asList(nationEntity));
		when(mapper.map(any(NationEntity.class), eq(NationDTO.class))).thenReturn(new NationDTO());

		List<NationDTO> result = nationService.selectNationList();

		assertNotNull(result);
		assertEquals(1, result.size());
	}
}