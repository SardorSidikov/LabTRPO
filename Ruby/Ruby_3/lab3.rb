regexp = /^(2[01].\d{2}.\d{4})
\s*(\w*\s)
\s*([[A-Za-z]\s?]*)
\.*(\d*.\d{2})
(.*)
$/x

transaction_dates = []
transaction_types = []
transaction_categories = []
transaction_sum = []
result = Hash.new(0)

File.open('sample.txt', 'r') do |f|
   f.each_line do |line|
     next unless matches = line.match(regexp)
     date, type, category, sum, = matches.captures
     transaction_dates.push(date)
     transaction_types.push(type)
     transaction_categories.push(category)
     transaction_sum.push(sum.to_f)
     result[category] += sum.to_f
   end
end

result.sort.to_h.each { |key, value| puts "#{key.strip}: #{value}" }
