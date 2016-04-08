import java.util.function.*;
import java.util.*;

public class L {
	// can't define generic static methods
	// lame
	
	/////////////
	//         //
	//  Basic  //
	//         //
	/////////////
	
	// 'a -> 'a
	public <A> A identity(A x) {
		return x;
	}
	
	// 'a -> unit
	public <A> Consumer<A> ignore() {
		return y -> {};
	}
	
	// 'a -> unit -> 'a
	public <A> Function<Object, A> constant(A x) {
		return y -> x;
	}
	
	// bool -> unit
	public void exIf(boolean x, String s) {
		if (x) throw new RuntimeException(s);
	}
	
	// (a' -> 'b -> 'c) -> 'b -> 'a -> 'c
	public <A, B, C> Function<B, Function<A, C>> swap(Function<A, Function<B, C>> f) {
		return y -> x -> f.apply (x).apply (y);
	}
	
	// ('a -> 'b) -> 'a -> 'a
	public <A> Function<A, A> tap(Function<A, Object> f) {
		return x -> {f.apply(x); return x;};
	}
	
	/////////////
	//         //
	//  Lists  //
	//         //
	/////////////
	
	// 'a -> 'a list -> 'a list
	public <A> List<A> cons(A x, List<A> l) {
		List<A> ans = new ArrayList<A>(length(l) + 1);
		ans.add(x);
		ans.addAll(l);
		return ans;
	}
	
	// 'a list -> bool
	public <A> Boolean isEmpty(List<A> l) {
		return l.isEmpty();
	}
	
	// 'a list -> int
	public <A> Integer length(List<A> l) {
		return l.size();
	}
	
	// (int * 'a list) -> 'a
	public <A> A nth(int n, List<A> l) {
		exIf(isEmpty(l), "Called nth on empty list");
		return l.get(n);
	}
	
	// 'a list -> 'a
	public <A> A head(List<A> l) {
		exIf(isEmpty(l), "Called head on empty list");
		return l.get(0);
	}
	
	// 'a list -> 'a list
	public <A> List<A> tail(List<A> l) {
		exIf(isEmpty(l), "Called tail on empty list");
		return l.subList(1, l.size() - 1);
	}
	
	// int -> int -> int list
	public Function<Integer, List<Integer>> range(Integer x) {
		return y -> {
			List<Integer> ans = new ArrayList<Integer>(y - x + 1);
			for (int i = x; i <= y; i++)
				ans.add(i);
			return ans;
		};
	}
	
	// int -> 'a -> 'a list
	public <A> Function<A, List<A>> create(Integer n) {
		return x -> {
			List<A> ans = new ArrayList<A>(n);
			for (int i = 0; i < n; i++)
				ans.add(x);
			return ans;
		};
	}
	
	// int -> (int -> 'a) -> 'a list
	public <A> Function<Function<Integer, A>, List<A>> init(Integer n) {
		return f -> {
			List<A> ans = new ArrayList<A>(n);
			for (int i = 0; i < n; i++) {
				ans.add(f.apply(i));
			}
			return ans;
		};
	}
	
	// 'a list -> 'a list
	public <A> List<A> clone(List<A> l) {
		return new ArrayList<A>(l);
	}
	
	// (int -> 'a -> unit) -> 'a list -> unit
	public <A> Consumer<List<A>> iteri(Function<Integer, Consumer<A>> f) {
		return l -> {
			Integer i = 0;
			for (A h : l) {
				f.apply(i).accept(h);
			}
		};
	}
	
	// ('a -> unit) -> 'a list -> unit
	public <A> Consumer<List<A>> iter(Consumer<A> f) {
		return iteri(x -> f);
	}
	
	// 'a list -> 'a list
	public <A> List<A> rev(List<A> l) {
		List<A> ans = new ArrayList<A>(length(l));
		iter((A h) -> ans.add(0, h));
		return ans;
	}
	
	// ('a -> 'b -> 'a) -> 'a -> 'b list -> 'a
	public <A, B> Function<A, Function<List<B>, A>> fold(Function<A, Function<B, A>> f) {
		List<A> ans = new LinkedList<A>();
		return a -> l -> {
			ans.set(0, a);
			iter((B h) -> ans.set(0, f.apply(ans.get(0)).apply(h))).accept(l);
			return ans.get(0);
		};
	}
	
	// ('a -> 'a -> 'a) -> 'a list -> 'a
	public <A> Function<List<A>, A> reduce(Function<A, Function<A, A>> f) {
		List<A> ans = new LinkedList<A>();
		return l -> {
			A a = head(l);
			ans.set(0, a);
			iter((A h) -> ans.set(0, f.apply(ans.get(0)).apply(h))).accept(tail(l));
			return ans.get(0);
		};
	}
	
	// ('a -> 'b -> 'a) -> 'a -> 'b list -> 'a list
	public <A, B> Function<A, Function<List<B>, List<A>>> scan(Function<A, Function<B, A>> f) {
		return a -> l -> {
			List<A> ans = new ArrayList<A>(length(l) + 1);
			ans.add(a);
			iter((B h) -> {
				ans.add(f.apply(ans.get(ans.size() - 1)).apply(h));
			}).accept(l);
			return ans;
		};
	}
	
	// (int -> 'a -> 'b) -> 'a list -> 'b list
	public <A, B> Function<List<A>, List<B>> mapi(Function<Integer, Function<A, B>> f) {
		return l -> {
			Integer i = 0;
			List<B> ans = new ArrayList<B>(length(l));
			iter((A h) -> ans.add(f.apply(i).apply(h)));
			return ans;
		};
	}
	
	// ('a -> 'b) -> 'a list -> 'b list
	public <A, B> Function<List<A>, List<B>> map(Function<A, B> f) {
		return mapi(x -> f);
	}
	
	// ('a -> bool) -> 'a list -> 'a
	public <A> Function<List<A>, A> find(Function<A, Boolean> f) {
		return l -> {
			for (A h : l) {
				if (f.apply(h)) {
					return h;
				}
			}
			return null;
		};
	}
	
	// ('a -> bool) -> 'a list -> 'a list
	public <A> Function<List<A>, List<A>> filter(Function<A, Boolean> f) {
		return l -> {
			List<A> ans = new ArrayList<A>(length(l));
			iter((A h) -> {if (f.apply(h)) ans.add(h);}).accept(l);
			return ans;
		};
	}
	
	// ('a -> bool) -> 'a list -> bool
	public <A> Function<List<A>, Boolean> forall(Function<A, Boolean> f) {
		return fold((Boolean a) -> (A h) -> a && f.apply(h)).apply(true);
	}
	
	
	// ('a -> bool) -> 'a list -> bool
	public <A> Function<List<A>, Boolean> exists(Function<A, Boolean> f) {
		return fold((Boolean a) -> (A h) -> a || f.apply(h)).apply(false);
	}
	
	// 'a -> 'a list -> bool
	public <A> Function<List<A>, Boolean> contains(A x) {
		return l -> l.contains(x);
	}
	
	// ('a -> 'b -> int) -> 'a list -> 'a list
	public <A> Function<List<A>, List<A>> sort(Comparator<A> f) {
		return l -> {
			List<A> ans = new ArrayList<A>(l);
			ans.sort(f);
			return ans;
		};
	}
	
	// 'a list -> 'a list
	public <A> List<A> uniq(List<A> l) {
		List<A> ans = new ArrayList<A>(length(l));
		Set<A> set = new HashSet<A>();
		iter((A h) -> {
			if (!set.contains(h)) {
				set.add(h);
				ans.add(h);
			};
		});
		return ans;
	}
	
	///////////////
	//           //
	//  2 Lists  //
	//           //
	///////////////
	
	// 'a list -> 'a list -> 'a list
	public <A> Function<List<A>, List<A>> append(List<A> l1) {
		return l2 -> {
			List<A> ans = new ArrayList<A>(l1);
			ans.addAll(l2);
			return ans;
		};
	}
	
	// 'a list -> 'b list -> bool
	public <A, B> Function<List<B>, Boolean> unequalLength(List<A> l1) {
		return l2 -> length(l1) == length(l2);
	}
	
	// (int -> 'a -> 'b -> unit) -> 'a list -> 'b list -> unit
	@SuppressWarnings("unchecked")
	public <A, B> Function<List<A>, Consumer<List<B>>> iteri2(Function<Integer, Function<A, Consumer<B>>> f) {
		return l1 -> l2 -> {
			exIf(unequalLength(l1).apply((List<Object>) l2), "Unequal lists");
			Iterator<A> i1 = l1.iterator();
			Iterator<B> i2 = l2.iterator();
			Integer i = 0;
			while (i1.hasNext()) {
				f.apply(i).apply(i1.next()).accept(i2.next());
				i++;
			}
		};
	}
	
	// ('a -> 'b -> unit) -> 'a list -> 'b list -> unit
	public <A, B> Function<List<A>, Consumer<List<B>>> iter2(Function<A, Consumer<B>> f) {
		return iteri2(i -> f);
	}
	
	// ('a -> 'b -> 'c -> 'a) -> 'a -> 'b list -> 'c list -> 'a
	public <A, B, C> Function<A, Function<List<B>, Function<List<C>, A>>> fold2(Function<A, Function<B, Function<C, A>>> f) {
		return a -> l1 -> l2 -> {
			List<A> ans = new ArrayList<A>(1);
			ans.set(0, a);
			iter2((B h1) -> (C h2) -> ans.set(0, f.apply(a).apply(h1).apply(h2)));
			return a;
		};
	}
	
	// (int -> 'a -> 'b -> 'c) -> 'a list -> 'b list -> 'c list
	public <A, B, C> Function<List<A>, Function<List<B>, List<C>>> mapi2(Function<Integer, Function<A, Function<B, C>>> f) {
		return l1 -> l2 -> {
			List<C> ans = new ArrayList<C>(length(l1));
			iteri2(i -> (A h1) -> (B h2) -> ans.add(f.apply(i).apply(h1).apply(h2)));
			return ans;
		};
	}
	
	// ('a -> 'b -> 'c) -> 'a list -> 'b list -> 'c list
	public <A, B, C> Function<List<A>, Function<List<B>, List<C>>> map2(Function<A, Function<B, C>> f) {
		return mapi2(i -> f);
	}

	// ('a -> 'b -> bool) -> 'a list -> 'b list -> bool
	public <A, B> Function<List<A>, Function<List<B>, Boolean>> forall2(Function<A, Function<B, Boolean>> f) {
		return fold2((Boolean a) -> (A h1) -> (B h2) -> a && f.apply(h1).apply(h2)).apply(true);
	}
	
	// ('a -> 'b -> bool) -> 'a list -> 'b list -> bool
	public <A, B> Function<List<A>, Function<List<B>, Boolean>> exists2(Function<A, Function<B, Boolean>> f) {
		return fold2((Boolean a) -> (A h1) -> (B h2) -> a || f.apply(h1).apply(h2)).apply(false);
	}
}
