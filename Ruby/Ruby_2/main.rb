require 'benchmark'

class PArray < Array
    
  def initialize(thread_count)
    @thread_count = thread_count
  end

  def any?(&predicate)
    process_parallel { |obj| obj.any? { |el| predicate.call(el) } }.any?
  end

  def all?(&predicate)
    process_parallel { |obj| obj.all? { |el| predicate.call(el) } }.all?
  end

  def map(&fn)
    process_parallel { |obj| obj.map { |el| fn.call(el) } }
      .reduce([]) { |memo, obj| memo + obj }
  end

  def select(&predicate)
    process_parallel { |obj| obj.select { |el| predicate.call(el) } }
      .reduce([]) { |memo, obj| memo + obj }
  end

  def process_parallel(&fn)
    each_slice(size / @thread_count)
      .map { |obj| Thread.new { fn.call(obj) } }
      .map { |thread| thread.join.value }
  end
               
end

custom_array = PArray.new(5)
arr = Array.new()
               
(1..1000000).each do |i|
   custom_array.push i
   arr.push i
end
               


               
puts(custom_array.any? { |obj| obj == 100 })
puts(custom_array.any? { |obj| obj == 0 })
#puts(custom_array.all? { |obj| obj % 2 == 0 })
#puts(custom_array.all? { |obj| obj % 2 == 1 })
#puts(custom_array.map { |obj| obj -99 }.to_s)
#puts(custom_array.select { |obj| obj > 400 }.to_s)


puts Benchmark.measure { custom_array.any? { |obj| obj == 100 } }
puts Benchmark.measure { custom_array.any? { |obj| obj == 0 } }
puts Benchmark.measure { custom_array.all? { |obj| obj % 2 == 0 } }
puts Benchmark.measure { custom_array.all? { |obj| obj % 2 == 1 } }
puts Benchmark.measure { custom_array.any? { |obj| obj -99 }.to_s }
puts Benchmark.measure { custom_array.any? { |obj| obj > 400 }.to_s }

puts("----------------------")
               
puts Benchmark.measure { arr.any? { |obj| obj == 100 } }
puts Benchmark.measure { arr.any? { |obj| obj == 0 } }
puts Benchmark.measure { arr.all? { |obj| obj % 2 == 0 } }
puts Benchmark.measure { arr.all? { |obj| obj % 2 == 1 } }
puts Benchmark.measure { arr.any? { |obj| obj -99 }.to_s }
puts Benchmark.measure { arr.any? { |obj| obj > 400 }.to_s }
