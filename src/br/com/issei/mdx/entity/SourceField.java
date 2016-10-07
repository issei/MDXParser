package br.com.issei.mdx.entity;

public class SourceField
{
	private String code;
	private String description;
	private String alias;
	private String type;
	private Integer fieldLen;
	private Integer fieldDec;
	
	
	public String getCode()
	{
		return code;
	}
	public String getDescription()
	{
		return description;
	}
	public String getAlias()
	{
		return alias;
	}
	public String getType()
	{
		return type;
	}
	public Integer getFieldLen()
	{
		return fieldLen;
	}
	public Integer getFieldDec()
	{
		return fieldDec;
	}
	public void setCode(String code)
	{
		this.code = code;
	}
	public void setDescription(String description)
	{
		this.description = description;
	}
	public void setAlias(String alias)
	{
		this.alias = alias;
	}
	public void setType(String type)
	{
		this.type = type;
	}
	public void setFieldLen(Integer fieldLen)
	{
		this.fieldLen = fieldLen;
	}
	public void setFieldDec(Integer fieldDec)
	{
		this.fieldDec = fieldDec;
	}
	@Override
	public String toString()
	{
		return "SourceField [code=" + code + ", description=" + description + ", alias=" + alias + ", type=" + type + ", fieldLen=" + fieldLen + ", fieldDec=" + fieldDec + "]";
	}
}
