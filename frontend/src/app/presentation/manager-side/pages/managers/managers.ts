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

  private formVazio(): ManagerDTO{
    return {nome: '', cpf:'', email:'', telefone:''};
  } //famosa GAMBIARRA, confie

  form: ManagerDTO = this.formVazio();

  ngOnInit(): void{
    this.managerService.carregarGerente().subscribe({
      error: (err) => console.log('não sobrou nada ao betinha', err),
    })
  }

  abrirVazio(): void{
    this.editandoID.set(null);
    this.form = this.formVazio();
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
    this.formAberto.set(false);
  }

  fecharForm(): void{
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
      // a validação mais porca q vc já viu
      return;
    }

    const id = this.editandoID();
    if(id){
      this.managerService.atualizarGerente(id, this.form).subscribe({
        next: () => {console.log('momento do popup de sucesso'); this.fecharForm},
        error: (err) => console.log('problemas na att de gerente', err),
      });
    }else{
      this.managerService.criarGerente(this.form).subscribe({
        next: () => {console.log('sucesso na criação de gerente'); this.fecharForm},
        error: (err) => console.log('nem criar tá craindo fi', err),
      });
    }

  }
 

}
