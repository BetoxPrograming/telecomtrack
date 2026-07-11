package com.telecomtrack.repository;

import com.telecomtrack.domain.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByCodigoUnico(String codigoUnico);

    boolean existsByCodigoUnicoAndIdMaterialNot(String codigoUnico, Long idMaterial);

    Optional<Material> findByCodigoUnico(String codigoUnico);

    @Query("SELECT m FROM Material m WHERE m.stockActual <= m.stockMinimo")
    List<Material> findMaterialesConStockBajo();
}