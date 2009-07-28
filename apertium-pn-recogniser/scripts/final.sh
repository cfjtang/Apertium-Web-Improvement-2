
# Removes *_Added_by_Proper_Noun_Detector_ or _Added_by_Proper_Noun_Detector_ (in case disabling the
# marking of unknown words has been selected)
sed -r 's/\*_Added_by_Proper_Noun_Detector_//g' |
sed -r 's/_Added_by_Proper_Noun_Detector_//g'

