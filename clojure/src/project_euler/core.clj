(ns project-euler.core
  (:gen-class)
  (:require [clojure.string :as str]
            [taoensso.tufte :as tufte :refer (defnp p profiled profile)]))

(tufte/add-basic-println-handler! {})
;; Utilities
(defn divides [x n]
  (zero? (mod n x)))

(def square #(* % %))
(defn int-pow [n e](int (Math/pow n e)))

(defn sqrt [n] (int (Math/sqrt n)))

(def sum #(reduce + %))

(defn first-digits-of
  ([x] (first-digits-of x (count (str x))))
  ([x n]
   (take n (map #(Integer/parseInt (str %))
             (str x)))))

(defn prime? [n]
  (let [root (Math/sqrt n)]
    (cond
      (<= n 1) false
      (= n 2) true
      :else (loop [d 3]
              (if (> d root)
                true
                (if (divides d n)
                  false
                  (recur (+ d 2))))))))

(defn factors-of [n]
  (-> (for [x (range 2 (inc(sqrt n)))
            :when (divides x n)]
        [x (/ n x)])

    (conj 1)
    flatten
    distinct))

;; Problem 1
(defn problem1 []
  (reduce + (filter (fn [x] (or (zero? (mod x 5))
                             (zero? (mod x 3))))
              (range 1000))))

;; Problem 2
(defn fib
  ([] (fib 0 1))
  ([a b] (lazy-seq
           (cons a (fib b
                     (+' a b))))))
(defn problem2 []
  (->> (take-while #(< % 4000000)
         (fib))
    (filter even?)
    (reduce +)))

;; Starting with 0, every third fibonacci number is even
(defn problem2-alt []
  (reduce +
    (take-while #(< % 4000000)
      (take-nth 3 (fib)))))

;; Problem 3
(defn add-to-multiple-set [coll n]
  (if (empty? coll)
    [n]
    (conj (remove #(divides n %) coll) n)))

(defn get-dividers [n]
  (loop [res []
         cur (int (Math/sqrt n))]
    (if (>= cur 2)
      (if (divides cur n)
        (recur (add-to-multiple-set res cur) (dec cur))
        (recur res (dec cur)))
      res)))

(defn problem3 []
  (apply max (get-dividers 600851475143)))

;; Problem 4
(defn palindrome? [n]
  (= (str n)
    (str/reverse (str n))))

(defn problem4 []
  (apply max (filter
               palindrome?
               (for [x (range 100 1000) y (range 100 1000)] (* x y)))))

;; Problem 5
(defn problem5 []
  (* 16 9 5 7 11 13 17 19))

;;Problem 6
(defn problem6 []
  (- (square (reduce + (range 101)))
    (reduce + (map square
                (range 101)))))

;;Problem 7
;; Finds 10001th prime number, 10000 in code because 2 is omitted
(defn problem7 []
  (last (take 10000
          (filter prime?
            (iterate #(+ % 2) 3)))))

;;Problem 8
(defn parse-digits
  ([st] (parse-digits st #""))
  ([st div]
   (map #(Integer/parseInt %)
     (str/split st div))))

(defn bigger
  "Take two values and choose the bigger of the two"
  [a b]
  (if (> a b)
    a
    b))

(defn problem8 []
  (let [input "7316717653133062491922511967442657474235534919493496983520312774506326239578318016984801869478851843858615607891129494954595017379583319528532088055111254069874715852386305071569329096329522744304355766896648950445244523161731856403098711121722383113622298934233803081353362766142828064444866452387493035890729629049156044077239071381051585930796086670172427121883998797908792274921901699720888093776657273330010533678812202354218097512545405947522435258490771167055601360483958644670632441572215539753697817977846174064955149290862569321978468622482839722413756570560574902614079729686524145351004748216637048440319989000889524345065854122758866688116427171479924442928230863465674813919123162824586178664583591245665294765456828489128831426076900422421902267105562632111110937054421750694165896040807198403850962455444362981230987879927244284909188845801561660979191338754992005240636899125607176060588611646710940507754100225698315520005593572972571636269561882670428252483600823257530420752963450"]
    (loop [index 0
           res 0]
      (let [end-index (+ index 13)
            mul (apply * (parse-digits
                           (subs input index end-index)))]
        (if (>= end-index (count input))
          res
          (recur (inc index)
            (bigger mul res)))))))

;;Problem 9
(defn problem9 []
  (->> (filter (fn [x] (= (square (:c x))
                        (+ (square (:a x))
                          (square (:b x)))))
         (for [a (range 1 998)
               b (range (inc a) 998)]
           {:a a :b b :c (- 1000 a b)}))
    first
    vals
    (reduce *)))


;;Problem 10
(defn problem10-old []
  (reduce + 2
    (filter prime?
      (take-while #(< % 2000000)
        (iterate #(+ % 2) 3)))))

(defn eratosthenes-sieve
  "Get set of all primes smaller than n"
  [n]
  (loop [nums (transient (set (cons 2 (range 3 (inc n) 2))))
         c 3]
    (if (> (square c) n)
      (persistent! nums)
      (recur (reduce disj! nums (range (square c) n c))
        (inc c)))))

(defn problem10 []
  (reduce + (eratosthenes-sieve 2000000)))

;;Problem 12
(defn count-factors-of [n]
  (loop [d 2
         acc []]
    (let [r (/ n d)]
      (cond (<= (Math/sqrt n) d) (+ 2 (count acc))
            (int? r) (recur (inc d) (conj acc d r))
            :else (recur (inc d) acc)))))

(def next-triangle-num (fn [[i s]] [(inc i) (+ i 1 s)]))

(defn problem12 []
  (-> (take-while (fn [[_ b]]
                    (< (count-factors-of b) 500))
        (iterate next-triangle-num [1 1]))
    last
    next-triangle-num))

;;Problem 13
(defn problem13 []
  (reduce str
    (-> [37107287533902102798797998220837590246510135740250 46376937677490009712648124896970078050417018260538 74324986199524741059474233309513058123726617309629 91942213363574161572522430563301811072406154908250 23067588207539346171171980310421047513778063246676 89261670696623633820136378418383684178734361726757 28112879812849979408065481931592621691275889832738 44274228917432520321923589422876796487670272189318 47451445736001306439091167216856844588711603153276 70386486105843025439939619828917593665686757934951 62176457141856560629502157223196586755079324193331 64906352462741904929101432445813822663347944758178 92575867718337217661963751590579239728245598838407 58203565325359399008402633568948830189458628227828 80181199384826282014278194139940567587151170094390 35398664372827112653829987240784473053190104293586 86515506006295864861532075273371959191420517255829 71693888707715466499115593487603532921714970056938 54370070576826684624621495650076471787294438377604 53282654108756828443191190634694037855217779295145 36123272525000296071075082563815656710885258350721 45876576172410976447339110607218265236877223636045 17423706905851860660448207621209813287860733969412 81142660418086830619328460811191061556940512689692 51934325451728388641918047049293215058642563049483 62467221648435076201727918039944693004732956340691 15732444386908125794514089057706229429197107928209 55037687525678773091862540744969844508330393682126 18336384825330154686196124348767681297534375946515 80386287592878490201521685554828717201219257766954 78182833757993103614740356856449095527097864797581 16726320100436897842553539920931837441497806860984 48403098129077791799088218795327364475675590848030 87086987551392711854517078544161852424320693150332 59959406895756536782107074926966537676326235447210 69793950679652694742597709739166693763042633987085 41052684708299085211399427365734116182760315001271 65378607361501080857009149939512557028198746004375 35829035317434717326932123578154982629742552737307 94953759765105305946966067683156574377167401875275 88902802571733229619176668713819931811048770190271 25267680276078003013678680992525463401061632866526 36270218540497705585629946580636237993140746255962 24074486908231174977792365466257246923322810917141 91430288197103288597806669760892938638285025333403 34413065578016127815921815005561868836468420090470 23053081172816430487623791969842487255036638784583 11487696932154902810424020138335124462181441773470 63783299490636259666498587618221225225512486764533 67720186971698544312419572409913959008952310058822 95548255300263520781532296796249481641953868218774 76085327132285723110424803456124867697064507995236 37774242535411291684276865538926205024910326572967 23701913275725675285653248258265463092207058596522 29798860272258331913126375147341994889534765745501 18495701454879288984856827726077713721403798879715 38298203783031473527721580348144513491373226651381 34829543829199918180278916522431027392251122869539 40957953066405232632538044100059654939159879593635 29746152185502371307642255121183693803580388584903 41698116222072977186158236678424689157993532961922 62467957194401269043877107275048102390895523597457 23189706772547915061505504953922979530901129967519 86188088225875314529584099251203829009407770775672 11306739708304724483816533873502340845647058077308 82959174767140363198008187129011875491310547126581 97623331044818386269515456334926366572897563400500 42846280183517070527831839425882145521227251250327 55121603546981200581762165212827652751691296897789 32238195734329339946437501907836945765883352399886 75506164965184775180738168837861091527357929701337 62177842752192623401942399639168044983993173312731 32924185707147349566916674687634660915035914677504 99518671430235219628894890102423325116913619626622 73267460800591547471830798392868535206946944540724 76841822524674417161514036427982273348055556214818 97142617910342598647204516893989422179826088076852 87783646182799346313767754307809363333018982642090 10848802521674670883215120185883543223812876952786 71329612474782464538636993009049310363619763878039 62184073572399794223406235393808339651327408011116 66627891981488087797941876876144230030984490851411 60661826293682836764744779239180335110989069790714 85786944089552990653640447425576083659976645795096 66024396409905389607120198219976047599490197230297 64913982680032973156037120041377903785566085089252 16730939319872750275468906903707539413042652315011 94809377245048795150954100921645863754710598436791 78639167021187492431995700641917969777599028300699 15368713711936614952811305876380278410754449733078 40789923115535562561142322423255033685442488917353 44889911501440648020369068063960672322193204149535 41503128880339536053299340368006977710650566631954 81234880673210146739058568557934581403627822703280 82616570773948327592232845941706525094512325230608 22918802058777319719839450180888072429661980811197 77158542502016545090413245809786882778948721859617 72107838435069186155435662884062257473692284509516 20849603980134001723930671666823555245252804609722 53503534226472524250874054075591789781264330331690]
      sum
      (first-digits-of 10))))

;;Problem 14
(defn collatz-iter [x]
  (loop [n x
         steps 1]
    (cond (= 1 n) [x steps]
          :else
          (recur
            (if (even? n)
              (/ n 2)
              (inc (* 3 n)))
            (inc steps)))))

(defn problem14 []
  (apply max-key second
    (pmap collatz-iter
      (range 3 1000000))))

;; Problem 16
;; Using fast modular exponentation, just for fun
(defn bits-of [n]
  (map #(Integer/parseInt (str %))
    (str (Integer/toBinaryString n))))

(defn fast-mod-exp
  "Calculate x^exp mod n"
  [x exp n]
  (loop [b (mod x n)
         bits (rest (bits-of exp))]
    (cond (empty? bits) b
          :else (recur (mod (*' b b
                              (if (= 1 (first bits))
                                x
                                1))
                         n)
                  (rest bits)))))

(defn problem16-ridiculous []
  (sum (map #(int (/ (fast-mod-exp 2 1000 %) (/ % 10)))
         (take (int (/ 1000 (Math/log10 1000)))
           (iterate #(* 10 %) 10N)))))

(defn problem16 []
  (sum(first-digits-of (.pow (BigInteger/valueOf 2) 1000))))


;;Problem 17
(def numbers
  {1 "one" 2 "two" 3 "three" 4 "four" 5 "five" 6 "six" 7 "seven" 8 "eight" 9 "nine"
   10 "ten" 11 "eleven" 12 "twelve" 13 "thirteen" 14 "fourteen" 15 "fifteen" 16 "sixteen"
   17 "seventeen" 18 "eighteen" 19 "nineteen" 20 "twenty" 30 "thirty" 40 "forty" 50 "fifty"
   60 "sixty" 70 "seventy" 80 "eighty" 90 "ninety"})

(defn- translate-num-xx [n]
  (if-let [num (numbers n)]
    num
    (str (numbers (* 10 (int (/ n 10))))
      (numbers (mod n 10)))))
(defn- translate-num-xxx [n]
  (str (numbers (int (/ n 100)))
    "hundred"
    (if-let [s (translate-num-xx (mod n 100))]
      (str "and" s)
      "")))

(defn translate-num [n]
  {:pre [(<= 1 n 1000)]}
  (case (count (str n))
    1 (numbers n)
    2 (translate-num-xx n)
    3 (translate-num-xxx n)
    4 "onethousand"))

(defn problem17 []
  (reduce + (map (comp count translate-num) (range 1 1001))))

;;Problem 18

(defn- read-file-lines [path transf]
  (mapv transf
    (str/split (slurp path)  #"\n")))

(defn- parse-triangle-file [path]
  (let [lines (read-file-lines path
                (comp vec
                  #(parse-digits % #" ")))]
    lines))

(defn- parent-at [tree n]
  (cond (<= 0 n (dec (count tree))) (nth tree n)
        :else 0))

(defn- max-parent [tree x]
  (max (parent-at tree (dec x))
    (parent-at tree x)))

(defn problem18
  ([] (problem18 (parse-triangle-file  "src/project_euler/18_triangle.txt")))
  ([triangle]
    (apply max
      (reduce
       (fn [acc line]
         (map-indexed (fn [i x]
                        (+ x (max-parent acc i)))
           line))
       []
       triangle))))
;;Problem 20
(defn problem20 []
  (-> (reduce * (BigInteger/valueOf 2) (range 3 101))
    first-digits-of
    sum))

;;Problem 21
(def mem-divisor-sum (memoize (comp sum factors-of)))

(defn problem21 []
  (sum (filter (fn [n]
                 (let [facsum (mem-divisor-sum n)]
                   (and (not= n facsum) (= n (mem-divisor-sum facsum)))))
         (range 1 10000))))

;;Problem 22
(defn name-score [name]
  (sum (map #(- (int %) 64) name)))

(defn problem22 []
  (sum
    (map-indexed
      (fn [idx e]
        (* (inc idx) (name-score e)))
      (-> "src/project_euler/22_names.txt"
        slurp
        (str/replace #"\"" "")
        (str/split #",")
        sort))))

;;Problem 23
(def abundant?
  (memoize
    #(< % (sum (factors-of %)))))

(defn sum-of-abundants? [n]
  (not (not-any? #(and (abundant? %)
                    (abundant? (- n %)))
         (range 1 (inc (int (/ n 2)))))))

(defn problem23 []
  (sum (remove sum-of-abundants?
         (range 1 28123))))

;;Problem 24

(defn swap
  "swap two values at the given indices in a vector"
  [v i1 i2]
  (assoc v i2 (v i1)
        i1 (v i2)))

(defn- longest-non-inc
  "Find the longest non-increasing subvectors first index"
  [set]
  (loop [i (dec (count set))]
    (if (and (> i 0)
          (>= (nth set (dec i))
            (nth set i)))
      (recur (dec i))
      i)))

(defn- rightmost-exceeder
  "Find the index of the first value greater than the value at index, starting from the end"
  [set index]
  (loop [i (dec (count set))]
    (if (>= (nth set i)
          (nth set index))
      i
      (recur (dec i)))))

(defn next-perm
  "The next lexicographic permutation"
  [set]
  (let [i (longest-non-inc set)
        pivot (dec i)
        j (rightmost-exceeder set pivot)
        nset (swap set pivot j)]
    (concat (subvec nset 0 i)
          (reverse (subvec nset i)))))

(defn problem24
  ([] (problem24 [0 1 2 3 4 5 6 7 8 9]
        1000000))
  ([set n]
   (apply str (second (take-nth n (iterate (comp vec next-perm) set))))))

;;Problem 25
(defn problem25 []
  (count (take-while #(> 1000 (count (str %)))
           (fib))))

;;Problem 26
;;https://oeis.org/A001913

(defn primitive-root? [g n]
  (= 1
    (fast-mod-exp g (dec n) n)))

(defn safe-prime [n primes]
  (contains? primes (/ (dec n) 2)))

(defn problem26 []
  (let [primes (eratosthenes-sieve 1000)]
    (first
      (for [n (sort > primes)
            :when (and (primitive-root? 10 n)
                    (safe-prime n primes))]
        n))))

;; Problem 67

(defn problem67 []
  (problem18 (parse-triangle-file "src/project_euler/67_triangle.txt")))
