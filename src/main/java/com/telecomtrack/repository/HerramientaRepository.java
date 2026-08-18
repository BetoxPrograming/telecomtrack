package com.telecomtrack.repository;

import com.telecomtrack.domain.Herramienta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HerramientaRepository extends JpaRepository<Herramienta, Integer> {

    Optional<Herramienta> findByCodigo(String codigo);

    long countByEstado(String estado);

    @Query("SELECT COUNT(DISTINCT h) FROM DetalleSolicitud d "
            + "JOIN d.herramienta h "
            + "WHERE d.solicitud.proyecto.supervisor.idUsuario = :idSupervisor "
            + "AND h.estado = :estado")
    long countDisponiblesPorSupervisor(@Param("idSupervisor") Integer idSupervisor,
                                        @Param("estado") String estado);

    @Query("SELECT h FROM Herramienta h WHERE h.estado = :estado "
            + "AND (:idUbicacion IS NULL OR h.ubicacion.idUbicacion = :idUbicacion) "
            + "AND (:texto IS NULL OR :texto = '' "
            + "     OR LOWER(h.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "     OR LOWER(h.codigo) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<Herramienta> buscarDisponibles(@Param("estado") String estado,
                                         @Param("idUbicacion") Integer idUbicacion,
                                         @Param("texto") String texto);
}
