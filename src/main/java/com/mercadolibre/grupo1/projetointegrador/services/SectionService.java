package com.mercadolibre.grupo1.projetointegrador.services;

import com.mercadolibre.grupo1.projetointegrador.dtos.SectionDTO;
import com.mercadolibre.grupo1.projetointegrador.entities.Section;
import com.mercadolibre.grupo1.projetointegrador.exceptions.EntityNotFoundException;
import com.mercadolibre.grupo1.projetointegrador.repositories.InboundOrderRepository;
import com.mercadolibre.grupo1.projetointegrador.repositories.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;

    /**
     * Busca se a sessao é valida passando o id da sessao e o id do armazem
     * @param sectionDto
     * @author Weverton Bruno
     */
    public Section findBySectionDto(SectionDTO sectionDto) {
        return sectionRepository.findByIdAndWarehouse_Id(sectionDto.getSectionCode(), sectionDto.getWarehouseCode())
                .orElseThrow(() -> new EntityNotFoundException("Sessão e/ou Armazem não encontrado na base de dados"));
    }
}
