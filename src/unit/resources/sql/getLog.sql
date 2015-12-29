select action, 
tranDate 
from `orbLog`.actionlog 
where tranDate > ? 
and tranDate < ? 
order by tranDate;