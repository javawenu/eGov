insert into eg_roleaction (actionid, roleid) select (select id from eg_action where name = 'Search ScheduleOfRate'), id from eg_role where name in ('Advertisement Tax Admin');