package com.monsterbank.ms_cliente.solicitacao;

import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.ListarSolicitacoesReturn;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoHateoasResponse;
import com.monsterbank.ms_cliente.solicitacao.solicitacaoDTOs.SolicitacaoResponseDTO;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class SolicitacaoModelAssembler implements RepresentationModelAssembler<SolicitacaoEntity, SolicitacaoHateoasResponse> {

    @Override
    public SolicitacaoHateoasResponse toModel(SolicitacaoEntity solicitacao) {
        SolicitacaoHateoasResponse model = new SolicitacaoHateoasResponse(
                solicitacao.getCpf(),
                solicitacao.getNome(),
                solicitacao.getEmail(),
                solicitacao.getTelefone(),
                solicitacao.getSalario().toString(),
                solicitacao.getEndereco(),
                solicitacao.getStatus().toString(),
                solicitacao.getMotivo(),
                solicitacao.getDataProcessamento() == null
                        ? null
                        : solicitacao.getDataProcessamento().toString()
        );
        addLinks(model, solicitacao);
        return model;
    }

    public SolicitacaoResponseDTO toResponseModel(SolicitacaoEntity solicitacao) {
        SolicitacaoResponseDTO model = new SolicitacaoResponseDTO(solicitacao);
        addLinks(model, solicitacao);
        return model;
    }

    public ListarSolicitacoesReturn toListModel(List<SolicitacaoEntity> solicitacoes) {
        ListarSolicitacoesReturn model = new ListarSolicitacoesReturn(
                solicitacoes.stream().map(this::toModel).toList()
        );
        model.add(
                linkTo(methodOn(SolicitacaoController.class)
                        .buscarSolicitacoes(null, 0))
                        .withSelfRel()
        );
        return model;
    }

    private void addLinks(RepresentationModel<?> model, SolicitacaoEntity solicitacao) {
        String cpf = solicitacao.getCpf();
        model.add(
                linkTo(methodOn(SolicitacaoController.class)
                        .buscaSolicitacaoPorCpf(cpf))
                        .withSelfRel(),
                linkTo(methodOn(SolicitacaoController.class)
                        .buscarSolicitacoes(null, 0))
                        .withRel("solicitacoes")
        );

        if (solicitacao.getStatus() == StatusSolicitacao.PENDENTE) {
            model.add(
                    Link.of("/solicitacoes/" + cpf + "/aprovacao").withRel("aprovacao"),
                    linkTo(methodOn(SolicitacaoController.class)
                            .rejeitarSolicitacao(cpf, null))
                            .withRel("rejeicao")
            );
        }
    }
}
