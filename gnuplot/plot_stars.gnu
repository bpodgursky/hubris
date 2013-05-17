set terminal postscript eps enhanced color "Times-Roman" 16
set output "stars.eps"

set style line 1 lt rgb "cyan" lw 3 pt 6
set style line 2 lt rgb "blue" lw 3 pt 6

set pointsize 2

set nokey

unset border 
unset xtics 
unset ytics 

set xrange [0:40]
set yrange [0:40]

set arrow from 8,30 to 4,20 ls 1
show arrow

plot 'stars.txt' using 1:2:3 with labels left offset 1,0 point ls 1, \
 'stars2.txt' using 1:2:3 with labels left offset 1,0 point ls 2,
