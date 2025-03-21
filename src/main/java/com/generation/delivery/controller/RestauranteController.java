package com.generation.delivery.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.generation.delivery.model.Restaurante;
import com.generation.delivery.repository.RestauranteRepository;
import com.generation.delivery.service.RestauranteService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/restaurantes")
@CrossOrigin(allowedHeaders = "*", origins = "*")
public class RestauranteController {

	@Autowired
	private RestauranteRepository restauranteRepository;
	
	@Autowired
	private RestauranteService restauranteService;

	@GetMapping
	public ResponseEntity<List<Restaurante>> getAll() {
		
		List<Restaurante> restaurantes = restauranteRepository.findAll();
		
		List<Restaurante> restaurantesComStatus = restaurantes.stream()
				.map(restaurante -> {
					if (restaurante.getHorarioAbertura() == null || restaurante.getHorarioFechamento() == null) {
						return restaurante;
					}
					restaurante.setStatus(restauranteService.isAberto(restaurante) ? "Aberto" : "Fechado");
					return restaurante;
				})
				.collect(Collectors.toList());
		
		return ResponseEntity.ok(restaurantesComStatus);
	}

	@GetMapping("/{id}")
	public ResponseEntity<Restaurante> getByid(@PathVariable Long id) {
		return restauranteRepository.findById(id).map(resposta -> ResponseEntity.ok(resposta))
				.orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
	}

	@GetMapping("/restaurantes/{razaoSocial}")
	public ResponseEntity<List<Restaurante>> getByTitulo(@PathVariable String razaoSocial) {
		return ResponseEntity.ok(restauranteRepository
				.findAllByRazaoSocialContainingIgnoreCase(razaoSocial));

	}

	@PostMapping
	public ResponseEntity<Restaurante> post(@Valid @RequestBody Restaurante restaurante) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(restauranteRepository.save(restaurante));
		
	}
	
	@PutMapping
	public ResponseEntity<Restaurante> put(@Valid @RequestBody Restaurante restaurante) {
		return restauranteRepository.findById(restaurante.getId())
        .map(resposta -> ResponseEntity.status(HttpStatus.CREATED)
        .body(restauranteRepository.save(restaurante)))
        .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
}

	@ResponseStatus(HttpStatus.NO_CONTENT)
	@DeleteMapping("/{id}")
	public void delete(@PathVariable Long id) {
		Optional<Restaurante> restaurante = restauranteRepository.findById(id);

		if (restaurante.isEmpty())
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);

		restauranteRepository.deleteById(id);

	}

}