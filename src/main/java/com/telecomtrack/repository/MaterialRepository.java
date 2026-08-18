package com.telecomtrack.repository;

import com.telecomtrack.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByCodigoUnico(String codigoUnico);

    boolean existsByCodigoUnicoAndIdMaterialNot(String codigoUnico, Long idMaterial);

    Optional<Material> findByCodigoUnico(String codigoUnico);

    @Query("SELECT m FROM Material m WHERE m.stockActual <= m.stockMinimo")
    List<Material> findMaterialesConStockBajo();

    @Query("SELECT DISTINCT d.material FROM DetalleMaterialEstimado d "
            + "WHERE d.listado.proyecto.supervisor.idUsuario = :idSupervisor "
            + "AND d.listado.estado = :estadoListado "
            + "AND d.material.stockActual <= d.material.stockMinimo")
    List<Material> findMaterialesConStockBajoPorSupervisor(
            @Param("idSupervisor") Integer idSupervisor,
            @Param("estadoListado") String estadoListado);

    @Query("SELECT m FROM Material m WHERE (:idUbicacion IS NULL OR m.ubicacion.idUbicacion = :idUbicacion) "
            + "AND (:texto IS NULL OR :texto = '' "
            + "     OR LOWER(m.nombre) LIKE LOWER(CONCAT('%', :texto, '%')) "
            + "     OR LOWER(m.codigoUnico) LIKE LOWER(CONCAT('%', :texto, '%')))")
    List<Material> buscarPorUbicacion(@Param("idUbicacion") Integer idUbicacion,
                                       @Param("texto") String texto);
}