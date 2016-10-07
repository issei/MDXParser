package br.com.issei.mdx.entity;
public class SourceError
{

	private String code;
	private String description;
	
	
	public String getCode()
	{
		return code;
	}
	public String getDescription()
	{
		return description;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	@Override
	public String toString()
	{
		return "SourceError [code=" + code + ", description=" + description + "]";
	}
}
