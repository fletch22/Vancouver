delete from `orblog`.actionLog 
where tranDate in (select distinct tranDate 
from `orblog`.undoactionlog 
where tranId >= ?);