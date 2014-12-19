# encoding: UTF-8
require 'rubygems'
require 'nokogiri'
require 'open-uri'
require 'htmlentities'
require 'active_support/all'


# Script by vigneshv
# GCI2014
# http://www.google-melange.com/gci/task/view/google/gci2014/5576140102041600

#Yiddish Letters   
letters = ["תּ","שׂ","ש","ר","ק","צ","פֿ","פּ","ע","ס","נ","מ","ל","כ","כּ","י","ט","ח","ז","ו","ה","ד","ג","ב","א"]
@yiddish_alpha = []

ROW_INDEX_OF_FIRST_ENTRY = 3
@possesive_match = /possesive/
@determiner_match = /[\/(]art[.]/
@conj_match = /[\/(]conj[.]/
@sing_match = /(sing[.]|singular)/
@plural_match = /plural/
@first_person_match = /(1st pers[.]|1st person)/
@second_person_match = /(2nd pers[.]|2nd person)/
@third_person_match = /(3rd pers[.]|3rd person)/
@pronoun_match = /[\/( ]pron[., ]|pronoun/
@dative_match = /[\/( ]dative/
@accusative_match = /([\/( ]accusative|[\/( ]accus[.])/
@interj_match = /([\/(]interj[.]|[\/(]int[.])/
@prep_match = /[\/(]prep[.]/
@name_match = /[\/(]name/
@adv_match = /[\/(]adv[.]/
@adj_match = /[\/(]adj[.]/
@vb_match = /[\/(]v[.]/
@vaux_match = /[\/(]v[.] auxiliary/
@vtrans_match = /[\/(]vt[.]/
@vi_match = /[\/(]vi[.]/

@masculinematch = /[\/( ]m[.]/
@femininematch = /[\/( ]f[.]/
@neutermatch = /[\/( ]n[.]/
@tbdmatch = /[(][.][)]/
@upcasematch = /\A[[:upper:]]/

@paren_blacklist = [/\(vulg[.,]/]

#Mistakes in the format {incorrect:correct}

mistake1_wrong = "אויסּקלײַב"
mistake1_correct = "אויסקלײַב" 

@yiddish_mistakes = {mistake1_wrong => mistake1_correct}

#This method will encode the part of speech as a Apertium part of speech
def get_pos_tags(partos, is_english, english_word)
  tags = []
  gender = get_gender(partos, is_english)

  if is_capitalized?(english_word)
    tags << "np"
    if !is_name?(partos)
      tags << "XX"
    end
  end
  
  if is_name?(partos)
    tags << "cog"
    if gender == ""
      tags << "XX"
    end
  end
  
  if gender != ""
    if !(tags.include?('np'))
      tags << "n"
    end
    
    if !is_english 
      tags << gender
    end
  end

  vt = verb_type(partos)
  if vt != ""
    if vt != "v"
      tags << "v"
    end
    tags << vt
  end

  if is_adjective?(partos) && !is_possessive?(partos)
    tags << "adj"
  end

  if is_adverb?(partos)
    tags << "adv"
  end

  if is_determiner?(partos)
    tags << "det"
  end

  if is_preposition?(partos)
    tags << "pr"
  end

  if (is_interjection?(partos) || has_exclam_at_end?(english_word))
    tags << "ij"
  end

  if is_pronoun?(partos)
    tags << "prn"
    tags << "XX"
  end
  
  if is_dative?(partos)
    tags << "dat"
  end
  
  if is_accusative?(partos)
    tags << "acc"
  end
  
  if is_possessive?(partos)
    tags << "det"
    tags << "pos"
  end

  num = number(partos)
  per = person(partos)

  if per != ""
    tags << per
  end
  
  if num != ""
    tags << num
  end





  #No distinction is made between coordinating/subordinating in the dictionary
  if is_conjuction?(partos)
    tags << "cnj"
    tags << "XX"
  end

  #Some parts of the dictionary are not directly taggable, like phrase.

  if tags == []
    tags << "XX"
  end
  
  tags
end

def get_gender(partos, english)
  mmatch = @masculinematch.match(partos)
  fmatch = @femininematch.match(partos)
  nmatch = @neutermatch.match(partos)
  tmatch = @tbdmatch.match(partos)
  gender = ""
  if mmatch
    gender += "m"
  end

  if fmatch
    gender += "f"
  end

  if nmatch
    if mmatch || fmatch
      gender += "n"
    else
      gender += "nt"
    end
  end

  if tmatch
    gender = "XX"
  end

  gender
end

def verb_type(partos)
  type = ""
  if (@vb_match.match(partos) || @vtrans_match.match(partos) || @vi_match.match(partos))
    type = "v"
    if(@vaux_match.match(partos))
      type = "vaux"
    end
  end
  type
end

def is_adjective?(partos)
  if (@adj_match.match(partos))
    return true
  end
  false
end

def is_adverb?(partos)
  if (@adv_match.match(partos))
    return true
  end
  false
end

def is_name?(partos)
  if (@name_match.match(partos))
    return true
  end
  false
end

def is_preposition?(partos)
  if (@prep_match.match(partos))
    return true
  end
  false
end

def is_interjection?(partos)
  if (@interj_match.match(partos))
    return true
  end
  false
end

def has_exclam_at_end?(word)
  if word[-1, 1] == "!"
    return true
  end
  false
end

def is_accusative?(partos)
  if @accusative_match.match(partos)
    return true
  end
  false
end

def is_dative?(partos)
  if (@dative_match.match(partos))
    return true
  end
  false
end

def is_pronoun?(partos)
  if (@pronoun_match.match(partos))
    return true
  end
  false
end

def person(partos)
  if @second_person_match.match(partos)
    return 'p2'
  elsif @first_person_match.match(partos)
    return 'p1'
  elsif @third_person_match.match(partos)
    return 'p3'
  end

  return ""
end

def number(partos)
  if @sing_match.match(partos)
    return 'sg'
  elsif @plural_match.match(partos)
    return 'pl'
  end

  return ""
end
# This dictionary does not have coordinating/subordinating distinctions so I mark them all as cnjXX...

def is_conjuction?(partos)
  if (@conj_match.match(partos))
    return true
  end
  false
end

def is_capitalized?(word)
  if @upcasematch.match(word)
    return true
  end
  return false
end

def is_determiner?(partos)
  if (@determiner_match.match(partos))
    return true
  end
  false
end

def strip_ellipse(word)
  return word.gsub(/\A\.{3,}|\.{3,}\z/,"")
end

def strip_a(word)
  match_a = /\Aa\s(.*)/.match(word) 
  if match_a
    return strip_ellipse(match_a[1])
  else
    return strip_ellipse(word)
  end
end

def is_possessive?(partos)
  return (@possesive_match.match(partos))
end

def remove_parens(word)
  txt = word.dup
  while txt.gsub!(/\([^()]*\)/,""); end
  
  @paren_blacklist.each do |pb|
    txt.gsub!(pb,"")
  end
  
  
  txt.gsub!(/\(.*/,"");
  
  txt
end

#END Helper methods


entries = []


#Add English letters to the existing array
letters = letters + ('A' .. 'Z').to_a
#crawl pages in http://www.yiddishdictionaryonline.com/
letters.each do |l|
  page = Nokogiri::HTML(open(URI::encode("http://www.yiddishdictionaryonline.com/dictionary/"+l)))
  webentries = page.xpath("//tr")
  webentries = webentries[ROW_INDEX_OF_FIRST_ENTRY,webentries.length]
  webentries.each do |e|
    full_entry_english = e.at_xpath('td[1]/font/text()').to_s.strip
    english = remove_parens(e.at_xpath('td[1]/font/text()').to_s.strip).strip
    quot_match = english.match(/"(.*)"/)
    if quot_match
      quot_match = quot_match[1].gsub(/\,/,"")
      english = english.gsub(/"(.*)"/,quot_match)
    end
    english = english.split(/\sor\s|[,;]/)
    
    english.each do |eng|
      if (eng.delete(' ') != "")
        entry={}
        entry[:english] = strip_a(eng.strip).strip
        entry[:full_entry_english] = full_entry_english
        entry[:yiddishromanized] = e.at_xpath('td[2]/font/text()').to_s.strip
        entry[:pronunciation] = e.at_xpath('td[3]/font/text()').to_s.strip
        entry[:pos] = e.at_xpath('td[4]/font/text()').to_s.strip
        yiddish = remove_parens(e.at_xpath('td[5]/font/b/a/text()').to_s.strip).strip.split(/\sor\s|[,;]/)
        full_entry_yiddish = e.at_xpath('td[5]/font/b/a/text()').to_s.strip
    
        yiddish.each_with_index do |yid,inx|
          entry_yid = {:english => entry[:english], :yiddishromanized => entry[:yiddishromanized], :pronunciation => entry[:pronunciation], :pos => entry[:pos], :full_entry_english => entry[:full_entry_english]}
          entry_yid[:yiddish] = ((strip_a(remove_parens(yid.strip)).strip).gsub(/["']/, ActiveSupport::Multibyte::Unicode.pack_graphemes([1524])))
          
          # Dictionary mistakes
          if @yiddish_mistakes.has_key?(entry_yid[:yiddish])
            entry_yid[:yiddish] = @yiddish_mistakes[entry_yid[:yiddish]] 
          end
          
          entry_yid[:full_entry_yiddish] = full_entry_yiddish
          ystrarr = entry_yid[:yiddish].split(" ")
          ystrarr.each do |yid|
            ystr = ActiveSupport::Multibyte::Unicode.unpack_graphemes(yid)
            ystr.each do |ys|
              letters = ActiveSupport::Multibyte::Unicode.pack_graphemes(ys.uniq)
              
              if !(@yiddish_alpha.include?(letters))
                @yiddish_alpha << letters
              end
            end
          end
          entry_yid[:count] = -1
          if yiddish.length > 1
            entry_yid[:count] = inx
          end
          entry_yid[:tags_eng] = get_pos_tags(entry_yid[:pos],true,entry[:english])
          entry_yid[:tags_yid] = get_pos_tags(entry_yid[:pos],false,entry[:english])
          entries << entry_yid
      end
      end
    end
  end
end

entries = entries.uniq {|e| [e[:english],e[:yiddish],e[:pos]]}
 

builder = Nokogiri::XML::Builder.new do |xml|
  xml.dictionary {
    xml.alphabet {
      @yiddish_alpha.sort.each do |yl|
        xml.text(yl.gsub(/[!"'-.12=?_\s]/,""))
      end
    }
    
    xml.section(:id => 'main', :type => 'standard') {
      entries.each do |ent|
        entry_header = {}
        if ent[:count] == 0
          entry_header[:c] = ent[:count].to_s
        elsif ent[:count] > 0
          entry_header[:slr] = ent[:count].to_s
        end
        xml.e(entry_header) {
          xml.p {
            xml.l {
              english_text = ent[:english].split(" ")
              english_text.each_with_index do |en,inx|
                xml.text(en)
                if inx != english_text.length-1
                  xml.b
                end
              end
              ent[:tags_eng].each do |tg|
                xml.s(:n=>tg)
              end
            }
            xml.r {
              yiddish_text = ent[:yiddish].split(" ")
              yiddish_text.each_with_index do |yi,inx|
                xml.text("#{yi.strip}")
                if inx != yiddish_text.length-1
                  xml.b
                end
              end
              ent[:tags_yid].each do |tg|
                xml.s(:n=>tg)
              end
            }
          }
          xml.text("\n")
          xml.comment("#{ent[:full_entry_english]}, #{ent[:full_entry_yiddish]}, #{ent[:yiddishromanized]}, #{ent[:yiddishromanized]}, #{ent[:pronunciation]}, #{ent[:pos]}")
        }
      end
    }
  }
end

File.open("apertium-yid-eng.eng-yid.dix", 'w') do |file|
  coder = HTMLEntities.new  
  file.write(coder.decode(builder.to_xml))
end
