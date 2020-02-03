def collect left, right = left
  left.reduce([]) do |tmp, first|
    tmp += right.reject { |same| first[-1] == same }
                 .map { |second| first + second }
  end
end

def process arr, size = 2
  (0...size-1).reduce(arr) { |tmp, _| collect tmp, arr }
end


list = ARGV[0...-1].sort
#list = list & list

begin
  length = ARGV.last.to_i
  puts process list, length
rescue

end

