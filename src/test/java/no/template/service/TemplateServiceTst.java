package no.template.service;

import no.template.generated.model.TemplateObject;
import no.template.model.TemplateObjectDB;
import no.template.repository.TemplateRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static no.template.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class TemplateServiceTst {

    @Mock
    private TemplateRepository templateRepository;

    @InjectMocks
    private TemplateService templateService;

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(templateRepository);
    }

    @Test
    public void getByIdNotFound() {
        Mockito
            .when(templateRepository.findById("123ID"))
            .thenReturn(Optional.empty());

        TemplateObject templateObject = templateService.getById("123ID");

        assertNull(templateObject);
    }

    @Test
    public void getById() {
        TemplateObjectDB persisted = getTEMPLATE_OBJECT_DB_0();
        Mockito
            .when(templateRepository.findById("123ID"))
            .thenReturn(Optional.of(persisted));

        TemplateObject templateObject = templateService.getById("123ID");

        assertEquals(persisted.getId().toHexString(), templateObject.getId());
        assertEquals(persisted.getName(), templateObject.getName());
        assertEquals(persisted.getUri(), templateObject.getUri());
    }

    @Test
    public void getAll() {
        List<TemplateObjectDB> persistedList = Collections.singletonList(getTEMPLATE_OBJECT_DB_0());
        Mockito
            .when(templateRepository.findAll())
            .thenReturn(persistedList);

        List<TemplateObject> list = templateService.getTemplateObjects(null);

        assertEquals(persistedList.get(0).getId().toHexString(), list.get(0).getId());
        assertEquals(persistedList.get(0).getName(), list.get(0).getName());
        assertEquals(persistedList.get(0).getUri(), list.get(0).getUri());
    }

    @Test
    public void getByName() {
        List<TemplateObjectDB> persistedList = Collections.singletonList(getTEMPLATE_OBJECT_DB_0());
        Mockito
            .when(templateRepository.findByNameLike("Name"))
            .thenReturn(persistedList);

        List<TemplateObject> list = templateService.getTemplateObjects("Name");

        assertEquals(persistedList.get(0).getId().toHexString(), list.get(0).getId());
        assertEquals(persistedList.get(0).getName(), list.get(0).getName());
        assertEquals(persistedList.get(0).getUri(), list.get(0).getUri());
    }

    @Test
    public void updateNotFound() {
        Mockito
            .when(templateRepository.findById("123ID"))
            .thenReturn(Optional.empty());

        TemplateObject templateObject = templateService.updateTemplateObject("123ID", getTEMPLATE_OBJECT_0());

        assertNull(templateObject);
    }
}
