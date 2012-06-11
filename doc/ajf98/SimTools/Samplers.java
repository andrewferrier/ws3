/*

Copyright (c) 2002, 2012, Andrew Ferrier. Portions based on work by, and (c) 
Tony Field 2000, 2001. All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are 
met: 

Redistributions of source code must retain the above copyright notice, 
this list of conditions and the following disclaimer.
 
Redistributions in binary form must reproduce the above copyright notice, 
this list of conditions and the following disclaimer in the documentation 
and/or other materials provided with the distribution. 
 
The name(s) of the author(s) of the software may not be used to endorse or 
promote products derived from this software without specific prior written 
permission.
 
THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS 
IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR 
PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF 
LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING 
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

*/

package doc.ajf98.SimTools;

import doc.ajf98.util.*;

/**
 * Tony Field's original version of this class is
 * considered by me to be version 1.0. I have made
 * some performance optimisations.
 *
 * @version 1.3.3.1
 */

public class Samplers
{
	public static class Constant extends DistributionSampler
	{
		public Constant(double value)
		{
			this.value = value;
		}

		public double next()
		{
			return value;
		}

		private double value;
	}

	public static class Erlang extends DistributionSampler
	{
		public Erlang(double k, double theta)
		{
			this.k = k;
			this.theta = theta;
			this.kTheta = k * theta;
		}

		public double next()
		{
			double acc = 1.0;

			for(int i = 1; i <= k; i++)
				acc *= random.nextDouble();

			return -Math.log(acc) / kTheta;
		}

		private double k, theta;
		private double kTheta;
	}

	public static class Exp extends DistributionSampler
	{
		public Exp(double rate)
		{
			this.rate = rate; 
		}

		public double next()
		{
			return -Math.log(random.nextDouble()) / rate;
		}

		private double rate;
	}

	// Gamma ---------------------------------------------------------
	// Only for integer values of beta, so Gamma( b, t ) is the same as
	// Erlang( b, t ).

	/* public static class Gamma extends DistributionSampler
	{
		private double m, b, theta, betatheta ;
		private int beta ;
		private int fact[] ;
		private GammaSampler gammaSampler ;
		private final double epsilon = 0.00001 ;

		public Gamma( double theta, int beta )
		{
			this.theta = theta ;
			this.beta = beta ;
			betatheta = beta * theta ;
			fact = new int[ beta ] ;
			int f = 1 ;
			for(int i = 0 ; i < beta ; i++)
			{
				fact[ i ] = f ;
				f *= ( i + 1 ) ;
			}
			double y = 0.0 ;
			m = f( ( beta - 1 ) / betatheta ) ;
			double x = 1.0, xold = 2.0 ;
			while(Math.abs( x - xold ) / x > epsilon)
			{
				xold = x ;
				x = xold - ( bigF( xold ) - 0.999 ) / f( xold ) ;
			} 
			b = x ;
			gammaSampler = new GammaSampler() ;
		}

		double bigF( double x )
		{
			double acc = 0.0 ;
			for(int i = 0 ; i < beta-1 ; i++)
				acc += Math.pow( betatheta * x, (float) i ) / fact[ i ] ;
			return 1 - acc * Math.exp( -betatheta * x ) ;
		}

		double f( double x )
		{
			return betatheta * Math.pow( betatheta * x, (float) beta - 1 ) *
				Math.exp( -betatheta * x ) / fact[ beta - 1 ] ;
		}

		public double next()
		{
			return gammaSampler.next() ;
		}

		private class GammaSampler extends RejectionMethod
		{
			public GammaSampler()
			{
				super(0, b, m);
			}

			double density(double x)
			{
				return f(x);
			}
		}
	} */

	public static class Normal extends DistributionSampler
	{
		public Normal( double mu, double sigma )
		{
			this.mu = mu;
			this.sigma = sigma;
		}

		public double next()
		{
			mustRedo = !mustRedo;

			if(mustRedo)
			{
				double r1 = random.nextDouble();
				       r2 = random.nextDouble();

				k = Math.sqrt(-2 * Math.log(r1)); 

				return k * Math.cos(TWOPI * r2) * sigma + mu; 
			}
			else
				return k * Math.sin(TWOPI * r2)	* sigma	+ mu;
		}

		private static final double TWOPI = 2 * Math.PI;
		private double mu, sigma, r2, k;
		private boolean mustRedo = false;
	}

	public static class PositiveNormal extends DistributionSampler
	{
        public PositiveNormal(double mu, double sigma)
		{
			norm = new Normal(mu, sigma);
		}

		public double next()
		{
			return Math.abs(norm.next());
		}

		private Normal norm;
	}

	public static class Uniform extends DistributionSampler
	{
		public Uniform( double a, double b )
		{
			this.a = a ;
			this.b = b ;
		}

		public double next()
		{
			return random.nextDouble() * (b - a) + a;
		}
		
		private double a, b; 
	}

	public static class Weibull extends DistributionSampler
	{
        public Weibull(double alpha, double beta)
		{
			this.alpha = alpha ;
			this.beta = beta ;
		}

		public double next()
		{
			return alpha * Math.pow(-Math.log(random.nextDouble()), 1.0 / beta);
		}

		private double alpha, beta;
	} 

	public static class Geometric extends DistributionSampler
	{
        public Geometric(double p)
		{
			q = Math.log(1 - p);
		}

		public double next()
		{
			return (int) (Math.log(random.nextDouble()) / q);
		}

		private double q;
	}

	public static class Pareto extends DistributionSampler
	{
		/*
		 * @param lambda must be greater than 0
		 */

		public Pareto(double a) throws IllegalArgumentException
		{
			if (!(a > 0))
				throw new IllegalArgumentException("The parameter must be greater than 0");

			this.minusovera = -(1 / a);
        }

		public double next()
		{
			return Math.pow(1 - random.nextDouble(), minusovera) - 1;
		}

		private double minusovera;
	}

	public static class ContinuousEmpirical extends DistributionSampler
	{
        public ContinuousEmpirical(double xs[], double fs[])
		{
			if(xs.length != (fs.length + 1) || fs.length == 0)
				throw new IllegalArgumentException("Empirical distribution array error");

			this.xs = new double[xs.length];
			System.arraycopy(xs, 0, this.xs, 0, xs.length);
			this.cs = new double[xs.length];

			double fTotal = 0.0;

			for(int i = 0; i < fs.length; i++)
				fTotal += fs[i];

			cs[0] = 0;

			for(int i = 0; i < fs.length; i++)
				cs[i + 1] = cs[i] + (fs[i] / fTotal);
		}

		public double next()
		{
			double r = random.nextDouble();
			int index = 0;

			while(r >= cs[index + 1])
				index++;

			return xs[index] + 
				(r - cs[index]) / (cs[index + 1] - cs[index]) *
				(xs[index + 1] - xs[index]) ;
		}

		private double xs[], cs[];
	}      

	public static class DiscreteEmpirical extends DistributionSampler
	{
        public DiscreteEmpirical(double xs[], double fs[])
		{
			if(xs.length != fs.length || fs.length == 0)
				throw new IllegalArgumentException("Empirical distribution array error");

			this.xs = new double[xs.length];
			System.arraycopy(xs, 0, this.xs, 0, xs.length);
			this.cs = new double[xs.length];

			double fTotal = 0.0;

			for(int i = 0; i < fs.length; i++)
				fTotal += fs[i];

			cs[0] = fs[0] / fTotal;

			for(int i = 1; i < fs.length; i++)
				cs[i] = cs[i - 1] + (fs[i] / fTotal);
		}

		public double next()
		{
			double r = random.nextDouble();
			int index = 0;

			while(r >= cs[index])
				index++;

			return xs[index];
		}

		private double xs[], cs[];
	}    

	/* private abstract static class RejectionMethod
	{
		private double a, b, m;

		public RejectionMethod(double a, double b, double m)
		{
			this.a = a;
			this.b = b;
			this.m = m;
		}

		abstract double density(double x);

		public double next()
		{
			Uniform ab = new Uniform(a, b);
			Uniform zerom = new Uniform(0, m);

			double x = ab.next();
			double y = zerom.next();

			int nrej = 0;

			while(y > density( x ))
			{
				x = ab.next();
				y = zerom.next();
				nrej++;
			}

			return x;
		}
	} */

	private static final Random random = Random.getRandom();
}
