package org.casper.proxy;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.casper.database.CasperDatabase;
import org.casper.exception.CasperQueryBuilderException;
import org.casper.query.QueryBuilder;
import org.casper.query.QueryPart;
import org.casper.repository.CasperRepository;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.StaticMethodMatcherPointcut;

import java.lang.reflect.Method;


public class CasperRepositoryFactory {

    private static CasperDatabase database = new CasperDatabase();

    @SuppressWarnings("unchecked")
    public static <T extends CasperRepository<?>> T getRepository(Class<T> iface) {
        database.createCollection(iface.getSimpleName());

        ProxyFactory pf = new ProxyFactory();
        Advisor advisor = new DefaultPointcutAdvisor(new CasperRepositoryPointcut(), new CasperRepositoryInterceptor());

        pf.addAdvisor(advisor);
        pf.setInterfaces(iface);
        pf.setTarget(iface);

        return (T) pf.getProxy();
    }

    private static class CasperRepositoryInterceptor implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invoc) throws Throwable {

            Method method = invoc.getMethod();
            Object[] args = invoc.getArguments();

            Class target = (Class) invoc.getThis();

            String repository = target.getSimpleName();
            String methodName = method.getName();

            if (methodName.equals("save")) {
                return database.save(repository, args[0]);
            } else if (methodName.equals("remove")) {
                return database.remove(repository, args[0]);
            } else if (methodName.equals("delete")) {
                return database.delete(repository, args[0]);
            } else if (methodName.equals("count")) {
                return database.count(repository);
            } else if (methodName.equals("findAll")) {
                return database.findAll(repository);
            } else if (methodName.equals("findOne")) {
                return database.findOne(repository, args[0]);
            } else if (methodName.startsWith("find")) {

                if (methodName.startsWith("findAll"))
                    methodName = "find" + methodName.substring(6);

                String s = methodName.substring(4);
                boolean single = false;
                if (s.startsWith("By")) {
                    s = s.substring(2);
                } else if (s.startsWith("OneBy")) {
                    single = true;
                    s = s.substring(5);
                } else {
                    throw new CasperQueryBuilderException("Invalid query type");
                }

                String[] parts = s.split("(?=\\p{Upper})");
                QueryBuilder qb = new QueryBuilder(repository, QueryBuilder.Type.FIND);
                String field = "";
                int partIndex = 0;
                int index = 0;
                QueryPart.Command command = QueryPart.Command.EqField;
                for (String p : parts) {
                    switch (p.toLowerCase()) {
                        case "not":
                            if (field.length() == 0) {
                                field += p;
                                break;
                            }
                            qb.add(QueryPart.Command.Not);
                            break;
                        case "like":
                            if (field.length() == 0) {
                                field += p;
                                break;
                            }
                            command = QueryPart.Command.LikeField;
                            break;
                        case "and":
                            if (partIndex > args.length)
                                throw new CasperQueryBuilderException("Invalid number of arguments");
                            qb.add(command, formatFieldName(field), args[partIndex]);
                            qb.add(QueryPart.Command.And);
                            field = "";
                            ++partIndex;
                            break;
                        case "or":
                            if (partIndex > args.length)
                                throw new CasperQueryBuilderException("Invalid number of arguments");

                            qb.add(command, formatFieldName(field), args[partIndex]);
                            qb.add(QueryPart.Command.Or);
                            field = "";
                            ++partIndex;
                            break;
                        case "limit":

                            if (index < parts.length - 1) {
                                field += p;
                                command = QueryPart.Command.EqField;
                                break;
                            }

                            if (field.length() > 0) {
                                if (partIndex > args.length)
                                    throw new CasperQueryBuilderException("Invalid number of arguments");
                                qb.add(command, formatFieldName(field), args[partIndex]);
                                field = "";
                                ++partIndex;
                            }

                            if (partIndex > args.length)
                                throw new CasperQueryBuilderException("Invalid number of arguments");

                            qb.add(QueryPart.Command.Limit, args[partIndex]);
                            ++partIndex;
                            break;
                        default:
                            command = QueryPart.Command.EqField;
                            field += p;

                    }

                    ++index;
                }

                if (field.length() > 0) {
                    if (partIndex > args.length)
                        throw new CasperQueryBuilderException("Invalid number of arguments");

                    qb.add(command, formatFieldName(field), args[partIndex]);
                }

                return single ? database.findOne(qb) : database.find(qb);
            }

            throw new CasperQueryBuilderException("Invalid query type");
        }

        private String formatFieldName(String field) {
            return Character.toLowerCase(field.charAt(0)) + field.substring(1);
        }
    }

    private static class CasperRepositoryPointcut extends StaticMethodMatcherPointcut {
        @Override
        public boolean matches(Method method, Class cls) {
            // TODO ideally we would handle this better
            return (method.getDeclaringClass().getSimpleName().endsWith("Repository"));
        }

        /*
        public ClassFilter getClassFilter() {
            return new ClassFilter() {
                public boolean matches(Class cls) {
                    return (cls == SomeBean.class);
                }
            }
        }
         */
    }
}