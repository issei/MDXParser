package br.com.issei.mdx.entity;

public class EntidadeValor {
	
	private String nome;
	private Object valor;
	private EntidadeValor pai;
	
	public EntidadeValor(String nome, Object valor, EntidadeValor pai) {
		super();
		this.nome = nome;
		this.valor = valor;
		this.pai = pai;
	}

	@Override
	public String toString() {
		return "[nome=" + nome + ", valor=" + valor + ", pai="
				+ pai + "]";
	}

	public String getNome() {
		return nome;
	}

	public Object getValor() {
		return valor;
	}

	public EntidadeValor getPai() {
		return pai;
	}
	
	public EntidadeValor setNome(String _nome)
	{
		return new EntidadeValor(_nome,this.valor,this.pai);
	}
	public EntidadeValor setValor(Object _valor)
	{
		return new EntidadeValor(this.nome,_valor,this.pai);
	}
	public EntidadeValor setPai(EntidadeValor _pai)
	{
		return new EntidadeValor(this.nome,this.valor,_pai);
	}
	
	

}
