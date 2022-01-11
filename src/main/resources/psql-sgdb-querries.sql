-- Show the number of lessons given per month during a specified year.
-- It shall be possible to retrieve the total number of lessons per month (just one number per month) and the specific
-- number of individual lessons, group lessons and ensembles (thrgroup by ee numbers per month).
-- This query is expected to be performed a few times per week.

select month                                                              as month,
       count(*)                                                           as lessons,
       SUM(case when lesson_type = 'individual lesson' THEN 1 ELSE 0 END) as individual_lesson,
       SUM(case when lesson_type = 'group lesson' THEN 1 ELSE 0 END)      as group_lesson,
       SUM(case when lesson_type = 'ensemble' THEN 1 ELSE 0 END)          as ensemble
From (
         SELECT EXTRACT('MONTH' from start_timestamp) as month_id,
                TO_CHAR(start_timestamp, 'Month')     as month,
                lesson_type
         FROM lesson
         where EXTRACT('YEAR' from start_timestamp) = :sel_year
     ) s
group by month, month_id
order by month_id;


 -- The same as above, but retrieve the average number of lessons per month during the entire year, instead of the total for each month.

SELECT SUM(count) / 12 as average_nr_lessons_:sel_year
from (
         SELECT count(*) as count
         FROM lesson
         where EXTRACT('YEAR' from start_timestamp) = :sel_year
         GROUP BY start_timestamp
         order by EXTRACT('MONTH' from start_timestamp)) s;


 -- List all instructors who has given more than a specific number of lessons during the current month.
 -- Sum all lessons, independent of type, and sort the result by the number of given lessons.
 -- This query will be used to find instructors risking to work too much, and will be executed daily.

Select *
from (
         SELECT concat(p.first_name, ' ', p.last_name) as full_name,
                count(*)                               as nr_of_jobs
         from lesson l
                  JOIN job j on l.id = j.lesson_id
                  JOIN instructor inst on inst.instructor_id = j.instructor_id
                  JOIN person p on inst.instructor_id = p.id
         where EXTRACT('MONTH' from l.start_timestamp) = EXTRACT('MONTH' from CURRENT_TIMESTAMP)
         group by inst.instructor_id, p.first_name, p.last_name) s
where nr_of_jobs >= :max_jobs_per_month;

 -- List all ensembles held during the next week, sorted by music genre and weekday.
 -- For each ensemble tell whether it's full booked, has 1-2 seats left or has more seats left.
 -- Hint: you might want to use a CASE statement in your query to produce the desired output.

SELECT l.lesson_type,
       l.start_timestamp,
       l.end_timestamp,
       concat(
               case when el.max_cap - count(*) = 0 THEN 'Fully booked.' ELSE '' END,
               case when el.max_cap - count(*) = 1 THEN 'Only one spot left!' ELSE '' END,
               case when el.max_cap - count(*) = 2 THEN 'Two spots left.' ELSE '' END
           )                 as status,
       count(*)              as bookings,
       el.max_cap,
       el.max_cap - count(*) as pax_left
from lesson as l
         JOIN ensamble_lesson el on l.id = el.lesson_id
         JOIN booking b on b.lesson_id = el.lesson_id
where EXTRACT('WEEK' from l.start_timestamp) = EXTRACT('WEEK' from CURRENT_TIMESTAMP) and EXTRACT('year' from l.start_timestamp) = EXTRACT('year' from CURRENT_TIMESTAMP)
group by b.lesson_id, el.max_cap, l.start_timestamp, l.end_timestamp, l.lesson_type;
