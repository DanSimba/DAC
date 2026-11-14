import { Component, computed, inject, OnInit, signal } from '@angular/core';
import { ManagerService } from '../../../../application/manager/services/manager-service';
import { ManagerDTO } from '../../../../domain/manager/models/managerDTO';
import { MatIcon } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-managers',
  imports: [MatIcon, FormsModule],
  templateUrl: './managers.html',
  styleUrl: './managers.css',
})

export class Managers implements OnInit{
  managerService = inject(ManagerService);
  managers = computed(()=> this.managerService.getManagers());
  local = inject(Location);
  
  formAberto = signal<boolean>(false);
  editandoID = signal<number | null>(null);
  public mensagemErro: string = '';

  private formVazio(): ManagerDTO{
    return {nome: '', cpf:'', email:'', telefone:''};
  } //famosa GAMBIARRA, confie

  form: ManagerDTO = this.formVazio();

  ngOnInit(): void{
    this.managerService.carregarGerente().subscribe({
      error: (err) => console.log('não sobrou nada ao betinha', err),
    })
  }

  // peguei do autocadastro se funcionar lá deve funcionar aqui
  public bloquearNaoNumeros(event: KeyboardEvent): void { 
    const tecla = event.key;
    const permitido = /\d/.test(tecla) || tecla === 'Backspace' || tecla === 'Delete' || tecla === 'ArrowLeft' || tecla === 'ArrowRight' || tecla === 'Tab' || tecla === 'Shift';

    if (!permitido) {
      event.preventDefault();
    }
  }

  public bloquearNumeros(event: KeyboardEvent): void {
    const tecla = event.key;
    if (/\d/.test(tecla)) {
      event.preventDefault();
    }
  }

  public formatarCPF(): void { // tambem peguei igual do autocadastro deve funcionar
    let cpfNumeros = this.form.cpf.replace(/\D/g, '').slice(0, 11);
    // cpfNumeros é só para apoio para pegar somente os numeros

    if (cpfNumeros.length > 9) {
      this.form.cpf = `${cpfNumeros.slice(0, 3)}.${cpfNumeros.slice(3, 6)}.${cpfNumeros.slice(6, 9)}-${cpfNumeros.slice(9)}`;
    } else if (cpfNumeros.length > 6) {
      this.form.cpf = `${cpfNumeros.slice(0, 3)}.${cpfNumeros.slice(3, 6)}.${cpfNumeros.slice(6)}`;
    } else if (cpfNumeros.length > 3) {
      this.form.cpf = `${cpfNumeros.slice(0, 3)}.${cpfNumeros.slice(3)}`;
    } else {
      this.form.cpf = cpfNumeros;
    }
  }

  public formatarTelefone(): void { // dale autocadastro dnv
    let telefoneNumeros = this.form.telefone.replace(/\D/g, '').slice(0, 11);

    if (telefoneNumeros.length > 7) {
      this.form.telefone = `(${telefoneNumeros.slice(0, 2)}) ${telefoneNumeros.slice(2, 7)}-${telefoneNumeros.slice(7)}`;
    } else if (telefoneNumeros.length > 2) {
      this.form.telefone = `(${telefoneNumeros.slice(0, 2)}) ${telefoneNumeros.slice(2)}`;
    } else {
      this.form.telefone = telefoneNumeros;
    }
  }

  abrirVazio(): void{
    this.editandoID.set(null);
    this.form = this.formVazio();
    this.mensagemErro= '';
    this.formAberto.set(true);
  }

  editarForm(manager: ManagerDTO): void{
    this.editandoID.set(manager.id ?? null);
    this.form = {
      nome: manager.nome,
      cpf: manager.cpf,
      email: manager.email,
      telefone: manager.telefone,
    }
    this.mensagemErro= '';
    this.formAberto.set(true);
  }

  fecharForm(): void{
    this.mensagemErro = '';
    this.formAberto.set(false);
  }

  remover(manager: ManagerDTO): void {
    if(!manager.id || !confirm(`Remover o gerente ${manager.nome}?`)){
      return;
    }

    this.managerService.removerGerente(manager.id).subscribe({
      error: (err) => console.log('gerente não removido!!', err), 
      // em algum momento rolará um popup aqui, confie
    })
  }

  salvar() {
    if(!this.form.nome || !this.form.cpf || !this.form.email){
      this.mensagemErro = 'Preencha todos os campos obrigatórios (*).';
      // a validação mais porca q vc já viu
      return;
    }

    this.mensagemErro = '';
    this.form.cpf = this.form.cpf.replace(/\D/g, '');
    this.form.telefone = this.form.telefone.replace(/\D/g, '');
    const id = this.editandoID();
    if(id){
      this.managerService.atualizarGerente(id, this.form).subscribe({
        next: () => {console.log('momento do popup de sucesso'); this.fecharForm()},
        error: (err) => {console.log('problemas na att de gerente', err);
        this.mensagemErro = 'Erro ao atualizar gerente';
        },
      });
    }else{
      this.managerService.criarGerente(this.form).subscribe({
        next: () => {console.log('sucesso na criação de gerente'); this.fecharForm()},
        error: (err) => {console.log('nem criar tá craindo fi', err);
        this.mensagemErro = 'Erro ao criar gerente';
        },
      });
    }

  }
 

}
