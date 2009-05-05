### DEPRECATED: use ``replace.py'' instead
### Inefficient script 
sed -i '/[0-9]/d' $1
sed -i 's/;/ /g' $1
sed -i 's/&quo//g' $1
sed -i 's/&am//g' $1
sed -i 's/&nbs//g' $1
sed -i 's/###//g' $1
sed -i '/^$/d' $1

