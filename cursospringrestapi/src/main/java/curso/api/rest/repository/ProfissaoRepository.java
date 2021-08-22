package curso.api.rest.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import curso.api.rest.model.Profissao;

public interface ProfissaoRepository extends JpaRepository<Profissao, Long>{

}
