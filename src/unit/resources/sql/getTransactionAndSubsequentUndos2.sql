select undoAction, tranDate 
from `orblog`.undoActionLog 
where tranId >= ? 
order by tranDate desc;