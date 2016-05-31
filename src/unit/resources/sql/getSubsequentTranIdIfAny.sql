  select tranId
  from `orblog`.undoactionlog
  where tranId > ?
  order by tranId
  limit 1;