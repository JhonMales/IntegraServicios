package com.proyectovegeta.seminario.repository;

import com.proyectovegeta.seminario.model.UsuarioMongo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioMongoRepository extends MongoRepository<UsuarioMongo, String> {
    UsuarioMongo findByEmail(String email);
    Optional<UsuarioMongo> findByUsuarioId(Long usuarioId);
}