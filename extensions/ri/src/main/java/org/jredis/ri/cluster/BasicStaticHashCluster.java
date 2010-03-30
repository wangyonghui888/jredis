/*
 *   Copyright 2009-2010 Joubin Houshyar
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *    
 *   http://www.apache.org/licenses/LICENSE-2.0
 *    
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.jredis.ri.cluster;

import java.util.Set;
import java.util.TreeMap;
import org.jredis.cluster.ClusterNodeSpec;
import org.jredis.cluster.ClusterSpec;
import org.jredis.cluster.model.ClusterNodeMap;
import org.jredis.cluster.model.StaticHashCluster;
import org.jredis.cluster.support.HashAlgorithm;

/**
 * As barebones as it gets. Uses the key's hashCode() (that is {@link byte[]#hashCode()})
 * to compute a node index, using a basic hashcode % nodeCnt as the index to the nodes list.
 * @author  joubin (alphazero@sensesay.net)
 * @date    Mar 30, 2010
 * 
 */

public class BasicStaticHashCluster extends StaticHashCluster.Support implements StaticHashCluster {
	
	private int nodeCnt;
	private ClusterNodeSpec[] nodes;
	/**
     * @param clusterSpec
     */
    protected BasicStaticHashCluster (ClusterSpec clusterSpec) {
	    super(clusterSpec);
    }

	/* (non-Javadoc) @see org.jredis.cluster.model.StaticHashCluster.Support#newHashAlgorithm() */
    @Override
    protected HashAlgorithm newHashAlgorithm () {
    	// TDOO: get this from the clusterspec
	    return new HashAlgorithm() {
			public long hash (byte[] kb) {
	            return kb.hashCode();
            }
	    };
    }

	/* (non-Javadoc) @see org.jredis.cluster.model.StaticHashCluster.Support#mapNodes() */
    @Override
    final protected void mapNodes () {
    	Set<ClusterNodeSpec> nodeSpecs = clusterSpec.getNodeSpecs();
    	nodeCnt = nodeSpecs.size();
    	nodes = new ClusterNodeSpec[nodeCnt];
    	nodes = nodeSpecs.toArray(nodes);
    }

	/* (non-Javadoc) @see org.jredis.cluster.model.StaticHashCluster.Support#newClusterNodeMap() */
    @Override
    protected ClusterNodeMap newClusterNodeMap () { return new NodeMap(); }

	/* (non-Javadoc) @see org.jredis.cluster.ClusterModel#getNodeForKey(byte[]) */
    public ClusterNodeSpec getNodeForKey (byte[] key) {
	    int nodeIdx = (int) (hashAlgo.hash(key)%nodeCnt);
	    return nodes[nodeIdx];
    }
    // ========================================================================
    // Inner Types
    // ========================================================================
    
	// ------------------------------------------------------------------------
	// ClusterNodeMap impl.
	// ------------------------------------------------------------------------
	
    @SuppressWarnings("serial")
    public class NodeMap extends TreeMap<Long, ClusterNodeSpec> implements ClusterNodeMap{
    }

}
