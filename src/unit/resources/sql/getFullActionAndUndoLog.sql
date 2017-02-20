select al.action,
al.tranDate,
ual.undoaction,
ual.tranId 
from `orbLog`.actionlog al 
left join `orbLog`.undoactionlog ual 
on al.tranDate = ual.tranDate 
order by tranDate;