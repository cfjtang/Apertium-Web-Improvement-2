echo "Translating the articles..."
./prevedi.sh
cd bhs-sl
echo "evaluating bhs->sl"
./evaluate.sh
echo "evaluating sl->bhs"
cd ../sl-bhs
./evaluate.sh
cd ..

echo
echo "The results of the evaluation are in their respective directories."
echo
