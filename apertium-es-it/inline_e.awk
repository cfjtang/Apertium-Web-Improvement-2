#!/usr/bin/gawk -f

BEGIN{
  in_e = 0
  e = ""
}
{
  if (index($0, "<e")) in_e = 1
  
  if (index($0, "</e>"))
  {
    printf e  
    in_e = 0
    e = ""
  }
  
  if (in_e)
  {
    e = e $0
  }
  else
  {
    print
  }
}