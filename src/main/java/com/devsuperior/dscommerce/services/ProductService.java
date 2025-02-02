package com.devsuperior.dscommerce.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscommerce.dto.ProductDTO;
import com.devsuperior.dscommerce.entities.Product;
import com.devsuperior.dscommerce.repositories.ProductRepository;
import com.devsuperior.dscommerce.services.exceptions.DatabaseException;
import com.devsuperior.dscommerce.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {

	@Autowired
	private ProductRepository repository;
	
	//CONSULTA POR ID
	@Transactional(readOnly = true)
	public ProductDTO findById(Long id) {
		Product product = repository.findById(id).orElseThrow(
				() -> new ResourceNotFoundException("Recurso não encontrado"));
		return new ProductDTO(product);
	}
	
	//CONSULTA TODOS PAGINADA
	@Transactional(readOnly = true)
	public Page<ProductDTO> findAll(Pageable pageable) {
		Page<Product> result = repository.findAll(pageable);
		return result.map(x -> new ProductDTO(x));
	}
	
	//INSERT	
	@Transactional
	public ProductDTO insert(ProductDTO dto) {
		Product entity = new Product();//PREPARAMOS O OBJETO
		copyDtoToEntity(dto, entity);
		entity = repository.save(entity);//OBJETO É SALVO
		return new ProductDTO(entity);//RETORNA O OBJETO SALVO E ATUALIZADO
	}
	
		//UPDATE	
		@Transactional
		public ProductDTO update(Long id, ProductDTO dto) {
			try {
				Product entity = repository.getReferenceById(id);
				copyDtoToEntity(dto, entity);
				entity = repository.save(entity);//OBJETO É SALVO
				return new ProductDTO(entity);//RETORNA O OBJETO SALVO E ATUALIZADO
			}
			catch (EntityNotFoundException e) {
				throw new ResourceNotFoundException("Recurso não encontrado");
			}
		}

		//DELETE POR ID
		@Transactional(propagation = Propagation.SUPPORTS)
		public void delete(Long id) {
			if(!repository.existsById(id)) {
				throw new ResourceNotFoundException("Recurso não encontrado");
			}
			try {
	        	repository.deleteById(id);    		
		}
	    	catch (DataIntegrityViolationException e) {
	        	throw new DatabaseException("Falha de integridade referencial");
	   	}
	}
		
		private void copyDtoToEntity(ProductDTO dto, Product entity) {
			entity.setName(dto.getName());
			entity.setDescription(dto.getDescription());
			entity.setPrice(dto.getPrice());
			entity.setImgUrl(dto.getImgUrl());
		}
		
		
	
}
