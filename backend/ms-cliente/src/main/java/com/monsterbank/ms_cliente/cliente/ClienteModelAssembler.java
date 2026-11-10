package com.monsterbank.ms_cliente.cliente;

import com.monsterbank.ms_cliente.ClienteController;
import com.monsterbank.ms_cliente.cliente.clienteDTOs.ClienteRetorno;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Component
public class ClienteModelAssembler implements RepresentationModelAssembler<ClienteEntity, ClienteRetorno> {

    @Override
    public ClienteRetorno toModel(ClienteEntity cliente) {
        ClienteRetorno model = new ClienteRetorno(
                cliente.getCpf(),
                cliente.getNome(),
                cliente.getEmail(),
                cliente.getTelefone(),
                cliente.getSalario(),
                cliente.getLogradouro(),
                cliente.getNumero(),
                cliente.getComplemento(),
                cliente.getCep(),
                cliente.getCidade(),
                cliente.getUf()
        );

        model.add(
                linkTo(methodOn(ClienteController.class).findByCpf(cliente.getCpf())).withSelfRel(),
                linkTo(methodOn(ClienteController.class).findNameByCpf(cliente.getCpf())).withRel("nome"),
                linkTo(methodOn(ClienteController.class).listarClientes(null, 0)).withRel("clientes")
        );

        return model;
    }
}
