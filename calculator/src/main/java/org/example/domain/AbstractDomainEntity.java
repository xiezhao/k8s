package org.example.domain;

import org.example.config.EntityState;
import org.example.config.RepositoryTemplateFactory;

import javax.persistence.*;

@MappedSuperclass
public abstract class AbstractDomainEntity {

    @Id
    @Column(
            name = "ID",
            insertable = true,
            updatable = false,
            nullable = false,
            length = 32,
            columnDefinition = "char(32)"
    )
    private String id;

    @Enumerated(EnumType.STRING)
    private EntityState entityState = EntityState.NORMAL;

    public AbstractDomainEntity() {
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    protected abstract String idPrefix();

    @PrePersist
    public void prePersist() {
        String id = IdGen.genId(this.idPrefix());
        this.setId(id);
    }

    public void save(){
        RepositoryTemplateFactory.getRepositoryTemplateJpa()
                .save(this);
    }

}
